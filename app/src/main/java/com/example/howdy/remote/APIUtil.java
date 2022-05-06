package com.example.howdy.remote;

public class APIUtil {

    private static final String API_URL = "http://10.107.144.22:3333/";

    public static com.example.howdy.remote.RouterInterface getInterface(){
        return com.example.howdy.remote.RetroFitClient.getClient(API_URL)
                .create(com.example.howdy.remote.RouterInterface.class);
    }
}
