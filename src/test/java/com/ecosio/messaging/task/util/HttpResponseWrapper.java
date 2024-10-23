package com.ecosio.messaging.task.util;

public class HttpResponseWrapper {
    private int statusCode;
    private String content;

    // Constructor
    public HttpResponseWrapper(int statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
    }

    // Getters and setters
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpResponseWrapper{" +
                "statusCode=" + statusCode +
                ", content='" + content + '\'' +
                '}';
    }
}
