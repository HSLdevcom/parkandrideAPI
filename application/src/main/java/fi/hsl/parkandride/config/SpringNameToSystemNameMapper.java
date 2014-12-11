package fi.hsl.parkandride.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class SpringNameToSystemNameMapper implements SmartApplicationListener {
    private final static Map<String, String> ENVIRONMENT_SYSTEM_PROPERTY_MAPPING = new HashMap<>();
    static {
        ENVIRONMENT_SYSTEM_PROPERTY_MAPPING.put("logging.path", "LOG_PATH");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();

            ENVIRONMENT_SYSTEM_PROPERTY_MAPPING.forEach((springName, systemName) -> {
                if (environment.containsProperty(springName)) {
                    System.setProperty(systemName, environment.getProperty(springName));
                }
            });
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return SpringApplication.class.isAssignableFrom(sourceType);
    }
}