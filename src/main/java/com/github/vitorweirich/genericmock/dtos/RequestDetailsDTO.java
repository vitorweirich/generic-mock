package com.github.vitorweirich.genericmock.dtos;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetailsDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String requestKey;
	// Null when matched by PATH
	private Matcher requestKeyMatcher;
	private MatchType matchedBy;
	private String method;
    private String uri;
    private String protocol;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String bodyString;
    
    public static RequestDetailsDTO fromRequest(HttpServletRequest request) {
    	 RequestDetailsDTO requestDetails = new RequestDetailsDTO();

         requestDetails.setMethod(request.getMethod());
         requestDetails.setUri(request.getRequestURI());
         requestDetails.setProtocol(request.getProtocol());

         Enumeration<String> headerNames = request.getHeaderNames();
         while (headerNames.hasMoreElements()) {
             String headerName = headerNames.nextElement();
             String headerValue = request.getHeader(headerName);
             requestDetails.getHeaders().put(headerName, headerValue);
         }

         Enumeration<String> parameterNames = request.getParameterNames();
         while (parameterNames.hasMoreElements()) {
             String parameterName = parameterNames.nextElement();
             String parameterValue = request.getParameter(parameterName);
             requestDetails.getParameters().put(parameterName, parameterValue);
         }
         
         // TODO: Criar função para dar parse no body com base nos headers (por ex: gzip, chunked) 
         try {
			requestDetails.setBodyString(request.getReader().lines().collect(Collectors.joining("\n")) );
		} catch (IOException e) {
			e.printStackTrace();
		}

         requestDetails.setRemoteAddr(request.getRemoteAddr());
         requestDetails.setRemoteHost(request.getRemoteHost());
         requestDetails.setRemotePort(request.getRemotePort());
         
         requestDetails.buildRequestKey();
         
         return requestDetails;
    }
    
    public void buildRequestKey() {
    	this.requestKey = this.getMethod() + "_" + this.getUri();
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
    
    public Optional<String> getNamedGroup(String groupName) {
    	if(Objects.isNull(this.getRequestKeyMatcher())) {
    		return Optional.empty();
    	}
    	return Optional.ofNullable(this.requestKeyMatcher.group(groupName));
    }
    
    public String getNamedGroup(String groupName, String defaultValue) {
    	return this.getNamedGroup(groupName).orElse(defaultValue);
    }
}
