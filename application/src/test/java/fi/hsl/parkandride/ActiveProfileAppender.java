package fi.hsl.parkandride;

import static java.util.Arrays.asList;
import static org.assertj.core.util.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.test.context.ActiveProfilesResolver;

import com.google.common.collect.Lists;

public abstract class ActiveProfileAppender implements ActiveProfilesResolver {
    private final List<String> profilesToAppend;

    public ActiveProfileAppender(String... profilesToAppend) {
        this.profilesToAppend = (List<String>) Arrays.asList(profilesToAppend);
    }

    @Override
    public String[] resolve(Class<?> testClass) {
        List<String> profiles = currentActiveProfiles();

        profilesToAppend.forEach((p) -> {
            if (!profiles.contains(p)) {
                profiles.add(p);
            }
        });

        return profiles.toArray(new String[profiles.size()]);
    }

    private List<String> currentActiveProfiles() {
        String springProfilesActive = System.getProperty("spring.profiles.active");
        if (isNullOrEmpty(springProfilesActive)) {
            springProfilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
        }

        List<String> profiles = new ArrayList<>();
        if (!isNullOrEmpty(springProfilesActive)) {
            profiles.addAll(asList(springProfilesActive.split(",")));
        }
        return profiles;
    }
}
