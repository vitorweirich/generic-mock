package com.github.vitorweirich.genericmock.handlers;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.github.vitorweirich.genericmock.dtos.RequestDetailsDTO;
import com.github.vitorweirich.genericmock.utils.ResourceLoaderUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostTestHandlerImpl implements RequestHandler {
	
	private static final Pattern HANDLER_PATTERN = Pattern.compile("(.*)_/v1/(.*)/batch/(.*)");

	private final ResourceLoaderUtil resourceLoaderUtil;
	
	@Override
	public Function<RequestDetailsDTO, ResponseEntity<Object>> getHandler() {
		return (request) -> {
			Matcher matcher = HANDLER_PATTERN.matcher(request.getRequestKey());
			matcher.find();
			String method = matcher.group(1);
			
			if("GET".equals(method)) {
				return ResponseEntity.ok("GetResponse"); 
			}
			
			String pathParam = matcher.group(3);
			
			Optional<Object> resource = this.resourceLoaderUtil.loadResource("mock.json", Object.class, (stringBody) -> stringBody.formatted(pathParam));
			
			if(resource.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(resource.get());
		};
	}

	@Override
	public String getPathMatcher() {
		return null;
	}

	@Override
	public Pattern getPatternMatcher() {
		return HANDLER_PATTERN;
	}

}
