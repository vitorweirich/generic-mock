package com.github.vitorweirich.genericmock.dtos;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetailsDTO {

	private String requestKey;
	private String method;
    private String uri;
    private String protocol;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String bodyString;
    
    public void buildRequestKey() {
    	this.requestKey = "%s_%s".formatted(this.getMethod(), this.getUri());
    }
    
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
