package com.audition.configuration;

import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private transient String dateFormat;
    private transient String locale;

    public Locale getParsedLocale() {
        if (locale == null || locale.isBlank()) {
            return Locale.US;
        }

        final String[] parts = locale.split("_");
        return new Locale(parts[0], parts.length > 1 ? parts[1] : "");
    }

    public String getDateFormat() {
        return dateFormat == null || dateFormat.isBlank() ? "yyyy-MM-dd" : dateFormat;
    }

}
