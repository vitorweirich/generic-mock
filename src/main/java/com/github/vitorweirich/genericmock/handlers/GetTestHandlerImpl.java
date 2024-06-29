package com.github.vitorweirich.genericmock.handlers;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.github.vitorweirich.genericmock.dtos.RequestDetailsDTO;

@Component
public class GetTestHandlerImpl implements RequestHandler {

	@Override
	public Function<RequestDetailsDTO, ResponseEntity<Object>> getHandler() {
		// TODO Auto-generated method stub
		return (request) -> ResponseEntity.ok("Get Handler Registerd With SUCCESS!");
	}

	@Override
	public String getPathMatcher() {
		return "GET_/v1/messages";
	}

	@Override
	public Pattern getPatternMatcher() {
		return null;
	}

}
