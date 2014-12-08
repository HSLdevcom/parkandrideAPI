package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.User;

public class AuthService {

    public void authorize(User currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException();
        }
    }

    public void authorize(User currentUser, Role requiredRole) {
        authorize(currentUser);
        if (currentUser.role != requiredRole) {
            throw new AccessDeniedException();
        }
    }

}
