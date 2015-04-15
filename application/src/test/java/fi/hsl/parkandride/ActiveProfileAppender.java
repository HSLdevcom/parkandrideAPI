// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride;

import static java.util.Arrays.asList;
import static org.assertj.core.util.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.test.context.ActiveProfilesResolver;

import com.google.common.collect.Lists;

public abstract class ActiveProfileAppender implements ActiveProfilesResolver {
    private final Set<String> profilesToAppend;

    public ActiveProfileAppender(String... profilesToAppend) {
        this.profilesToAppend = new LinkedHashSet<>(Arrays.asList(profilesToAppend));
    }

    @Override
    public String[] resolve(Class<?> testClass) {
        Set<String> profiles = currentActiveProfiles();
        profiles.addAll(profilesToAppend);
        return profiles.toArray(new String[profiles.size()]);
    }

    private Set<String> currentActiveProfiles() {
        String springProfilesActive = System.getProperty("spring.profiles.active");
        if (isNullOrEmpty(springProfilesActive)) {
            springProfilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
        }

        Set<String> profiles = new LinkedHashSet<>();
        if (!isNullOrEmpty(springProfilesActive)) {
            profiles.addAll(asList(springProfilesActive.split(",")));
        }
        return profiles;
    }
}
