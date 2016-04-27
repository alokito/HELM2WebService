package org.helm.lambda;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by saldaal1 on 07/03/16.
 */
public class RootHandlerTests {

    Context context = new Context() {
        public String getAwsRequestId() {
            return null;
        }

        public String getLogGroupName() {
            return null;
        }

        public String getLogStreamName() {
            return null;
        }

        public String getFunctionName() {
            return null;
        }

        public String getFunctionVersion() {
            return null;
        }

        public String getInvokedFunctionArn() {
            return null;
        }

        public CognitoIdentity getIdentity() {
            return null;
        }

        public ClientContext getClientContext() {
            return null;
        }

        public int getRemainingTimeInMillis() {
            return 0;
        }

        public int getMemoryLimitInMB() {
            return 0;
        }

        public LambdaLogger getLogger() {
            return new LambdaLogger() {
                public void log(final String string) {
                    System.out.println(string);
                }
            };
        }
    };

    @Test
    public void TestParseSimple() {
        RootHandler rootHandler = new RootHandler();
        String input = "{\n" +
                " \"body\" : \"Foo\",\n" +
                " \"operation\" : \"POST\",\n" +
                " \"stage\" : \"test-invoke-stage\",\n" +
                " \"request_id\" : \"test-invoke-request\",\n" +
                " \"api_id\" : \"API123\",\n" +
                " \"resource_path\" : \"/\",\n" +
                " \"resource_id\" : \"RES123\",\n" +
                " \"http_method\" : \"POST\",\n" +
                " \"source_ip\" : \"test-invoke-source-ip\",\n" +
                " \"user-agent\" : \"Apache-HttpClient/4.3.4 (java 1.5)\",\n" +
                " \"account_id\" : \"1234\",\n" +
                " \"api_key\" : \"test-invoke-api-key\",\n" +
                " \"caller\" : \"ABCD1234\",\n" +
                " \"user\" : \"ABCD1234\",\n" +
                " \"user_arn\" : \"arn:aws:iam::1234:user/USER1\",\n" +
                "\n" +
                " \"params\" : {\n" +
                " \"path\" : {\n" +
                " }\n" +
                " ,            \"querystring\" : {\n" +
                " }\n" +
                " ,            \"header\" : {\n" +
                " }\n" +
                " }\n" +
                " }";
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        rootHandler.parsePath(new ByteArrayInputStream(input.getBytes()), os, context);
        Assert.assertEquals(os.toString(), "/: Foo");
    }
    @Test
    public void TestParsePath1() {
        RootHandler rootHandler = new RootHandler();
        String input = "{\n" +
                " \"body\" : \"Foo\",\n" +
                "\n" +
                " \"params\" : {\n" +
                " \"path\" : {\n" +
                "\"path1\" : \"people\"" +
                " }\n" +
                " ,            \"querystring\" : {\n" +
                " }\n" +
                " ,            \"header\" : {\n" +
                " }\n" +
                " }\n" +
                " }";
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        rootHandler.parsePath(new ByteArrayInputStream(input.getBytes()), os, context);
        Assert.assertEquals(os.toString(), "/people: Foo");
    }
    @Test
    public void TestParsePath2() {
        RootHandler rootHandler = new RootHandler();
        String input = "{\n" +
                " \"body\" : \"Foo\",\n" +
                "\n" +
                " \"params\" : {\n" +
                " \"path\" : {\n" +
                "\"path1\" : \"people\"," +
                "\"path2\" : \"5\"" +
                " }\n" +
                " ,            \"querystring\" : {\n" +
                " }\n" +
                " ,            \"header\" : {\n" +
                " }\n" +
                " }\n" +
                " }";
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        rootHandler.parsePath(new ByteArrayInputStream(input.getBytes()), os, context);
        Assert.assertEquals(os.toString(), "/people/5: Foo");
    }
    @Test
    public void TestValidationValid() {
        RootHandler rootHandler = new RootHandler();
        String input = "{\n" +
                " \"body\" : \"Foo\",\n" +
                " \"params\" : {\n" +
                "   \"path\" : {\n" +
                "     \"path1\" : \"Validation\",\n" +
                "     \"path2\" : \"PEPTIDE1%7BA.R.C.A.A.K.T.C.D.A%7D$PEPTIDE1,PEPTIDE1,8:R3-3:R3$$$\"\n" +
                "   },             \n" +
                "   \"querystring\" : {},\n" +
                "   \"header\" : {}\n" +
                " }\n" +
                "}";

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        rootHandler.route(new ByteArrayInputStream(input.getBytes()), os, context);
        Assert.assertEquals(os.toString(), "{\"Validation\":\"valid\",\"HELMNotation\":\"PEPTIDE1{A.R.C.A.A.K.T.C.D.A}$PEPTIDE1,PEPTIDE1,8:R3-3:R3$$$\"}");

    }
    @Test
    public void TestValidationInvalid() {
        RootHandler rootHandler = new RootHandler();
        String input = "{\n" +
                " \"body\" : \"Foo\",\n" +
                " \"params\" : {\n" +
                "   \"path\" : {\n" +
                "     \"path1\" : \"Validation\",\n" +
                "     \"path2\" : \"PEPTDE1%7BA.R.C.A.A.K.T.C.D.A%7D$PEPTIDE1,PEPTIDE1,8:R3-3:R3$$$\"\n" +
                "   },             \n" +
                "   \"querystring\" : {},\n" +
                "   \"header\" : {}\n" +
                " }\n" +
                "}";

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        rootHandler.route(new ByteArrayInputStream(input.getBytes()), os, context);
        Assert.assertEquals(os.toString().contains("\"Status\":400"), true);

    }
}
