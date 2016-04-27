package org.helm.lambda.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.helm.rest.Application;


public class LocalServerConfig {

    public final static URI BASE_URI = UriBuilder.fromUri("http://localhost").port(9998).build();
    private static HttpServer server;


    public static void startServer() {
        System.out.println("Starting Grizzly server...");
        server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new Application());
        System.out.println("ServerInformation " + server.toString());
        System.out.println(String.format("Jersey application started with WADL availabe at %sapplication.wadl", BASE_URI));

    }


    public static Client testClient () {
        return ClientBuilder.newBuilder().register(JacksonFeature.class).build();
    }

}
