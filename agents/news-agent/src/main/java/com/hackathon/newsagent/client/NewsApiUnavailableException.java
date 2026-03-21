package com.hackathon.newsagent.client;

public class NewsApiUnavailableException extends RuntimeException {

    public NewsApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
