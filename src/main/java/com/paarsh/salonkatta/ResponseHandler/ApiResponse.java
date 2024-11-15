package com.paarsh.salonkatta.ResponseHandler;

public class ApiResponse {
    private boolean status;
    private String message = null;
    private int responseCode;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;

    public ApiResponse() {
    }


    public ApiResponse(Object object, boolean status, int responseCode,String message) {
        this.object=object;
        this.status = status;
        this.message = message;
        this.responseCode = responseCode;
    }
    public ApiResponse( boolean status, int responseCode, String message) {
        this.status = status;
        this.message = message;
        this.responseCode = responseCode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

}
