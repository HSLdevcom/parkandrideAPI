package fi.hsl.parkandride.core.service;

public class AuthenticationRequiredException extends RuntimeException {
    public AuthenticationRequiredException() {
        super(null, null, true, false);
    }
}
