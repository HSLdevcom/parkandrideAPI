package fi.hsl.parkandride.core.domain;

public class CapacityPropertyPathTranslator {
    public String translate(String input) {
        for (CapacityType ct : CapacityType.values()) {
            String target = "capacities[" + ct.name() + "]";
            String replacement = "capacities." + ct.name();

            String translated = input.replace(target, replacement);
            if (!translated.equals(input)) {
                return translated;
            }
        }
        return input;
    }
}
