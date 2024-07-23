package com.github.vitorweirich.genericmock.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitorweirich.genericmock.dtos.MatchType;
import com.github.vitorweirich.genericmock.dtos.RequestDetailsDTO;
import com.github.vitorweirich.genericmock.handlers.RequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GenericMockController {
	
	private final Map<String, Function<RequestDetailsDTO, ResponseEntity<Object>>> pathHandlers = new HashMap<>();
	private final Map<Pattern, Function<RequestDetailsDTO, ResponseEntity<Object>>> patternHandlers = new HashMap<>();
	private final ObjectMapper objectMapper;
	
	public GenericMockController(List<RequestHandler> registeredHandlers, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		
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
    public ResponseEntity<Object> handleAllRequests(HttpServletRequest request) {
        RequestDetailsDTO requestDetails = RequestDetailsDTO.fromRequest(request);
        
        if(log.isDebugEnabled()) {
        	log.debug("GenericMockController.handleAllRequests - requestData [{}]", this.toIdentJson(requestDetails));
        }
        
        return Optional.ofNullable(pathHandlers.get(requestDetails.getRequestKey()))
        		.map(handler -> {
        			requestDetails.setMatchedBy(MatchType.PATH);
        			return handler;
        		})
        		.or(() -> Optional.ofNullable(firstMatch(requestDetails)))
        		.map(handler -> handler.apply(requestDetails))
        		.orElse(ResponseEntity.ok("OK"));
    }
    
    private Function<RequestDetailsDTO, ResponseEntity<Object>> firstMatch(RequestDetailsDTO requestDetails) {
    	for(Entry<Pattern, Function<RequestDetailsDTO, ResponseEntity<Object>>> handler: patternHandlers.entrySet()) {
    		Matcher matcher = handler.getKey().matcher(requestDetails.getRequestKey());
    		if(matcher.matches()) {
    			requestDetails.setMatchedBy(MatchType.PATTERN);
    			requestDetails.setRequestKeyMatcher(matcher);
    			return handler.getValue();
    		}
    	}
    	
    	return null;
    }
    
    private String toIdentJson(Object object) {
    	try {
			return this.objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
}
