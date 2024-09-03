package com.fahad.integraatask;

public class LoginResponse {
    private String Token;
    private String TrackingToken;
    private String userid;
    private String type;


    public String getToken() {
        return Token;
    }
    public void setToken(String token) {
        this.Token = token;
    }


    public String getTrackingToken() {
        return TrackingToken;
    }
    public void setTrackingToken(String trackingToken) {
        TrackingToken = trackingToken;
    }


    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
