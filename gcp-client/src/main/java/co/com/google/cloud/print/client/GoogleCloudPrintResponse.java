package co.com.google.cloud.print.client;

import org.codehaus.jackson.JsonNode;

public class GoogleCloudPrintResponse {
    private int httpStatus;
    private String message;
    private JsonNode data;

    public JsonNode getData() {
            return data;
    }
    public void setData(JsonNode data) {
            this.data = data;
    }
    public int getHttpStatus() {
            return httpStatus;
    }
    public void setHttpStatus(int httpStatus) {
            this.httpStatus = httpStatus;
    }
    public String getMessage() {
            return message;
    }
    public void setMessage(String message) {
            this.message = message;
    }
}