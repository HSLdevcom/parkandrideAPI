package fi.hsl.parkandride.core.service;

import java.beans.Introspector;
import java.io.InputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslationService {
    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    private JsonNode translations;

    public TranslationService() {
        try (InputStream translStream = getClass().getResourceAsStream("/static/assets/translations-fi.json")) {
            translations = new ObjectMapper().readTree(translStream);
        } catch (Exception e) {
            log.warn("Failed to read translation file", e);
        }
    }

    public String translate(Enum<?> value) {
        if (value == null) {
            return null;
        }
        String translationGroup = Introspector.decapitalize(value.getClass().getSimpleName());
        JsonNode node = translations.path(translationGroup).path(value.name());
        if (node.isMissingNode()) {
            node = translations.findValue(value.name());
        }
        if (node != null) {
            node = node.get("label");
        }
        return node != null ? node.asText() : value.name();
    }
}
