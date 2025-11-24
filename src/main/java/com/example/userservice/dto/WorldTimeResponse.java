package com.example.userservice.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WorldTimeResponse {
    @JsonProperty("datetime")
    private String datetime;

    public WorldTimeResponse() { }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

