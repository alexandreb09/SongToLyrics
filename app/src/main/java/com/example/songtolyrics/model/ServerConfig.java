package com.example.songtolyrics.model;

public class ServerConfig {

    private String IP;
    private String port;
    private  boolean HTTPS;

    public ServerConfig(String IP, String port, boolean HTTPS) {
        this.IP = IP;
        this.port = port;
        this.HTTPS = HTTPS;
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

    public boolean isHTTPS() {
        return HTTPS;
    }

    public void setHTTPS(boolean HTTPS) {
        this.HTTPS = HTTPS;
    }

    public String getHTTPStr(){
        if (HTTPS){
            return "https";
        }
        return "http";
    }

    public String getUrl(String method){
        return this.getUrl() + "/" + method;
    }

    public String getUrl(){
        return this.getHTTPStr() + "://" + IP + ":" + port;
    }
}
