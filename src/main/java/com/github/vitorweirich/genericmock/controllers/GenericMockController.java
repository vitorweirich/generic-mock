package com.github.vitorweirich.genericmock.controllers;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.vitorweirich.genericmock.dtos.RequestDetailsDTO;
import com.github.vitorweirich.genericmock.handlers.RequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GenericMockController {
	
	private final Map<String, Function<RequestDetailsDTO, ResponseEntity<Object>>> pathHandlers = new HashMap<>();
	private final Map<Pattern, Function<RequestDetailsDTO, ResponseEntity<Object>>> patternHandlers = new HashMap<>();
	
	public GenericMockController(List<RequestHandler> registeredHandlers) {
		registeredHandlers.forEach(handler -> {
			if(Objects.nonNull(handler.getPathMatcher())) {
				this.pathHandlers.put(handler.getPathMatcher(), handler.getHandler());
			}
			if(Objects.nonNull(handler.getPatternMatcher())) {
				this.patternHandlers.put(handler.getPatternMatcher(), handler.getHandler());
			}
		});
	}

    @RequestMapping("/**")
    public ResponseEntity<Object> handleAllRequests(HttpServletRequest request, @RequestBody(required = false) String body) {
        RequestDetailsDTO requestDetails = getRequestDetails(request, body);
        log.debug("GenericMockController.handleAllRequests - requestData [{}]", requestDetails.toString());
        
        return Optional.ofNullable(pathHandlers.get(requestDetails.getRequestKey()))
        		.or(() -> Optional.ofNullable(firstMatch(requestDetails)))
        		.map(handler -> handler.apply(requestDetails))
        		.orElse(ResponseEntity.ok("OK"));
    }
    
    private Function<RequestDetailsDTO, ResponseEntity<Object>> firstMatch(RequestDetailsDTO requestDetails) {
    	for(Entry<Pattern, Function<RequestDetailsDTO, ResponseEntity<Object>>> handler: patternHandlers.entrySet()) {
    		if(handler.getKey().matcher(requestDetails.getRequestKey()).matches()) {
    			return handler.getValue();
    		}
    	}
    	
    	return null;
    }

    private RequestDetailsDTO getRequestDetails(HttpServletRequest request, String body) {
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
        
        requestDetails.setBodyString(body);

        requestDetails.setRemoteAddr(request.getRemoteAddr());
        requestDetails.setRemoteHost(request.getRemoteHost());
        requestDetails.setRemotePort(request.getRemotePort());

        requestDetails.buildRequestKey();
        
        return requestDetails;
    }
    
}
