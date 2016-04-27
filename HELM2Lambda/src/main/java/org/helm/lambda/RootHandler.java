package org.helm.lambda;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import org.helm.lambda.config.LocalServerConfig;
import org.json.JSONString;

/**
 * Created by saldaal1 on 03/03/16.
 *
 */
public class RootHandler {
    final static List<String> propNames = ImmutableList.of(
        "file.separator",
        "java.class.path",
        "java.home"	,
        "java.vendor",
        "java.vendor.url",
        "java.version",
        "line.separator",
        "os.arch",
        "os.name",
        "os.version",
        "path.separator",
        "user.dir",
        "user.home"	,
        "user.name"
    );
    static {
        LocalServerConfig.startServer();
    }

    private String ifNull(String maybe, String def) {
        return maybe ==null || maybe.length() == 0?def:maybe;
    }
    public void route(InputStream inputStream, OutputStream outputStream, Context context) {
        ObjectMapper objectMapper = new ObjectMapper();
        LambdaRequest request = null;
        final LambdaLogger logger = context.getLogger();;

        try {
            request = objectMapper.readValue(inputStream, LambdaRequest.class);
            final String body = request.getBody();
            final String method = StringUtils.upperCase(ifNull(request.getHttp_method(),"GET"));
            final JsonNode headerParams = request.getParams().get("header");
            final String accepts = ifNull(getTextSubNode(headerParams, "Accept"), MediaType.APPLICATION_JSON);
            final String contentType = ifNull(getTextSubNode(headerParams, "Content-type"), MediaType.APPLICATION_JSON);
            logger.log(method + ", body: " + body);
            JsonNode pathParams = request.getParams().get("path");
            // note: the API gateway will already encode the paths, so { will be %7B and } will be %7D.
            final String path1 = getTextSubNode(pathParams, "path1");
            final String path2 = getTextSubNode(pathParams, "path2");
            final String path3 = getTextSubNode(pathParams, "path3");
            final String path4 = getTextSubNode(pathParams, "path4");

            final String fullPath = "" + (path1.length() > 0 ? path1 : "") +
                    (path2.length() > 0 ? '/' + path2 : "") +
                    (path3.length() > 0 ? '/' + path3 : "") +
                    (path4.length() > 0 ? '/' + path4 : "");
            logger.log("full path: "+fullPath);
            logger.log("accept: "+accepts);

            UriBuilder builder = UriBuilder.fromUri(LocalServerConfig.BASE_URI)
                    .path(fullPath);

            JsonNode query = request.getParams().get("querystring");
            Iterator<String> it = query.fieldNames();
            while (it.hasNext()) {
                String name = it.next();
                builder = builder.queryParam(name, query.textValue());
            }
            final URI uri = builder.build();

            final Client client = LocalServerConfig.testClient();
            Entity<String> e = "GET".equals(method)?null:Entity.entity(body, contentType);
            Response response = client.target(uri).request(accepts).method(method,e);
            if (response.getStatus() == 200) {
                String val = response.readEntity(String.class);

                outputStream.write(val.getBytes());
            } else {
                try {
                    final JSONObject obj = new JSONObject()
                            .accumulate("Status", response.getStatus())
                            .accumulate("path1", path1)
                            .accumulate("path2", path2)
                            .accumulate("path3", path3)
                            .accumulate("path4", path4)
                            .accumulate("Path", fullPath);
                    final String message = obj.toString();
                    outputStream.write((message).getBytes());
                } catch (JSONException ex) {
                    final String msg = "JSONException: " + ex.getMessage();
                    outputStream.write(msg.getBytes());
                    logger.log(msg);
                }
            }
            client.close();
        } catch (IOException e) {
            final String msg = "IOException: " + e.getMessage();
            logger.log(msg);
        }
    }

    public void parsePath(InputStream inputStream, OutputStream outputStream, Context context) {
        final LambdaLogger logger = context.getLogger();
        logger.log("parsePath called");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LambdaRequest request = objectMapper.readValue(inputStream, LambdaRequest.class);
            logger.log("Body: " + request.getBody());
            JsonNode pathParams = request.getParams().get("path");
            String path1 = getTextSubNode(pathParams, "path1");
            String path2 = getTextSubNode(pathParams, "path2");
            String str = "/"+(path1.length() > 0?path1:"") + (path2.length() > 0?'/'+path2:"") + ": " + request.getBody();
            outputStream.write(str.getBytes());
        } catch (IOException e) {
            logger.log("IOException: " + e.getMessage());
        }
    }

    private String getTextSubNode(final JsonNode node, final String name) {
        if (node == null) {
            return null;
        }
        JsonNode subNode = node.get(name);
        return subNode == null?"": subNode.textValue();
    }

    public void echo(InputStream inputStream, OutputStream outputStream, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            String str = CharStreams.toString(new InputStreamReader(inputStream));
            outputStream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String toString(final ClientContext clientContext) {
        if (clientContext == null) {
            return "Null client context;\n";
        } else {

            String ctx = "Client: " + clientContext.getClient() + ";\n";
            ctx = addMap(ctx, "Custom", clientContext.getCustom());
            ctx = addMap(ctx, "Environment", clientContext.getEnvironment());
            return ctx;
        }
    }

    private String addMap(String ctx, final String name, final Map<String, String> map) {
        ctx += name +"\n";
        for (String key : map.keySet()) {
            ctx += key +": " + map.get(key);
        }
        return ctx;
    }


    public String getProps() {
        String props = "";
        for (String name : System.getProperties().stringPropertyNames()) {
            props += name +": " + System.getProperty(name) +";" +'\n';
        }
        return props;
    }
    public String getNetwork()  {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            String net = "";
            while(e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                net += "Network Interface " + n.getName() + ": " + n.getHardwareAddress() +";"+'\n';
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    net += i.getHostAddress() +": " + i.getCanonicalHostName() + ";" + '\n';
                }
            }
            return net;
        } catch (SocketException e1) {
            return "SocketException: " + e1.getMessage();
        }

    }
}
