package com.github.vitorweirich.genericmock.handlers;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;

import com.github.vitorweirich.genericmock.dtos.RequestDetailsDTO;

public interface RequestHandler {

	Function<RequestDetailsDTO, ResponseEntity<Object>> getHandler();
	
	String getPathMatcher();
	
	Pattern getPatternMatcher();
}
