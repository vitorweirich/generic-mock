package com.github.vitorweirich.genericmock.utils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResourceLoaderUtil {

	private final ObjectMapper objectMapper;
	
	private final ResourceLoader resourceLoader;

	public <T> T toObject(String jsonString, final Class<T> clazz) {
        try {
            return this.objectMapper.readValue(jsonString, clazz);
        } catch (final Exception exception) {
        	exception.printStackTrace();
            throw new RuntimeException("ParseJsonError");
        }
    }
	
	public <T> Optional<T> loadResource(String resourceName, Class<T> clazz) {
    	return this.loadResource(resourceName, clazz, null);
    }
	
	public <T> Optional<T> loadResource(String resourceName, Class<T> clazz, Function<String, String> bodyStringTransformer) {
    	try {
    		Resource resource = resourceLoader.getResource("classpath:mocks/%s".formatted(resourceName));
    		
    		if(Objects.nonNull(bodyStringTransformer)) {
    			String contentAsString = resource.getContentAsString(StandardCharsets.UTF_8);
    			String transformed = bodyStringTransformer.apply(contentAsString);
    			return Optional.of(objectMapper.readValue(transformed, clazz));
    		}
    		
    		T object = objectMapper.readValue(resource.getInputStream(), clazz);
    		
    		return Optional.of(object);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
    }
	
}
