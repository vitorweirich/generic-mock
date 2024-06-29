package com.github.vitorweirich.genericmock.configuration;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import com.github.vitorweirich.genericmock.dtos.RequestDetailsDTO;

@Configuration
@ImportRuntimeHints(ResourceRuntimeHints.ResourcesRegistrar.class)
public class ResourceRuntimeHints {
    static class ResourcesRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        	hints.serialization().registerType(RequestDetailsDTO.class);
            hints.resources()
                    .registerPattern("mocks/*.json");
        }
    }
}
