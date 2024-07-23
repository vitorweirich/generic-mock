package com.github.vitorweirich.genericmock.handlers;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.github.vitorweirich.genericmock.dtos.RequestDetailsDTO;
import com.github.vitorweirich.genericmock.utils.ResourceLoaderUtil;

@Component
public class PostTestHandlerImpl extends BaseHandler {
	
	private static final Pattern HANDLER_PATTERN = Pattern.compile("(?<method>.*)_/v1/(?<apiName>.*)/batch/(?<operationId>.*)");
	
	public PostTestHandlerImpl(ResourceLoaderUtil resourceLoaderUtil) {
		super(resourceLoaderUtil);
	}
	
	@Override
	public Function<RequestDetailsDTO, ResponseEntity<Object>> getHandler() {
		return request -> {
			String method = request.getNamedGroup("method", "GET");
            Optional<String> apiName =  request.getNamedGroup("apiName");
            String operationId = request.getNamedGroup("operationId", "");
            
            System.out.println();
            System.out.println(request.getBodyString());
            System.out.println();
            Object object = this.resourceLoaderUtil.toObject(request.getBodyString(), Object.class);
            
            System.out.println();
            System.out.println(request.getMatchedBy());
            System.out.println(method);
            System.out.println(apiName);
            System.out.println(operationId);
            System.out.println(object);
            System.out.println();
			
			if("GET".equals(method)) {
				return ResponseEntity.ok("GetResponse"); 
			}
			
			Optional<Object> resource = this.resourceLoaderUtil.loadResource("mock.json", Object.class, stringBody -> stringBody.formatted(operationId));
			
			if(resource.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(resource.get());
		};
	}

	@Override
	public String getPathMatcher() {
		return "GET_/sim";
	}

	@Override
	public Pattern getPatternMatcher() {
		return HANDLER_PATTERN;
	}

}
