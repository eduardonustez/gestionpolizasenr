package com.gestionpolizas.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverterFactory(new EnumSinMayusculasConverterFactory());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class EnumSinMayusculasConverterFactory implements ConverterFactory<String, Enum> {

        @Override
        @NonNull
        public <T extends Enum> Converter<String, T> getConverter(@NonNull Class<T> tipo) {
            return valor -> valor.isBlank() ? null : (T) Enum.valueOf(tipo, valor.trim().toUpperCase(Locale.ROOT));
        }
    }
}
