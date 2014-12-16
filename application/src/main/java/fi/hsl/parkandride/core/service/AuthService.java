package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.domain.Role;
import fi.hsl.parkandride.core.domain.User;

public class AuthService {

    public void authorize(User currentUser, Role... requiredRoles) {
        if (currentUser == null) {
            throw new AccessDeniedException();
        }
        if (requiredRoles.length == 0) {
            return;
        }
        for (Role requiredRole : requiredRoles) {
            if (currentUser.role == requiredRole) {
                return;
            }
        }
        throw new AccessDeniedException();
    }

}
