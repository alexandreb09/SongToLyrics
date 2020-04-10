package com.example.songtolyrics.model;

public class ServerConfig {

    private String IP;
    private String port;
    private  boolean HTTP;

    public ServerConfig(String IP, String port, boolean HTTP) {
        this.IP = IP;
        this.port = port;
        this.HTTP = HTTP;
    }


    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isHTTP() {
        return HTTP;
    }

    public void setHTTP(boolean HTTP) {
        this.HTTP = HTTP;
    }

    public String getHTTPStr(){
        if (HTTP) return "http";
        else return "https";
    }

    public String getUrl(String method){
        return this.getHTTPStr() + "://" + IP + ":" + port + "/" + method;
    }

    public String getUrl(){
        return this.getHTTPStr() + "://" + IP + ":" + port;
    }
}
