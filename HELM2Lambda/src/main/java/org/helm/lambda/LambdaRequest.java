package org.helm.lambda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LambdaRequest {
    private String body;
    private String operation;
    private String stage;
    private String request_id;
    private String api_id;
    private String resource_path;
    private String resource_id;
    private String http_method;
    private String source_ip;
    private String agent;
    private String api_key;
    private String caller;
    private String user;
    private String user_arn;
    private JsonNode params;

    public JsonNode getParams() {
        return params;
    }

    public void setParams(final JsonNode params) {
        this.params = params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(final String operation) {
        this.operation = operation;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(final String stage) {
        this.stage = stage;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(final String request_id) {
        this.request_id = request_id;
    }

    public String getApi_id() {
        return api_id;
    }

    public void setApi_id(final String api_id) {
        this.api_id = api_id;
    }

    public String getResource_path() {
        return resource_path;
    }

    public void setResource_path(final String resource_path) {
        this.resource_path = resource_path;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(final String resource_id) {
        this.resource_id = resource_id;
    }

    public String getHttp_method() {
        return http_method;
    }

    public void setHttp_method(final String http_method) {
        this.http_method = http_method;
    }

    public String getSource_ip() {
        return source_ip;
    }

    public void setSource_ip(final String source_ip) {
        this.source_ip = source_ip;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(final String agent) {
        this.agent = agent;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(final String api_key) {
        this.api_key = api_key;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(final String caller) {
        this.caller = caller;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getUser_arn() {
        return user_arn;
    }

    public void setUser_arn(final String user_arn) {
        this.user_arn = user_arn;
    }
}
