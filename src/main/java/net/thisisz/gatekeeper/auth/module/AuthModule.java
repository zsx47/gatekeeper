package net.thisisz.gatekeeper.auth.module;

import java.util.UUID;

public interface AuthModule {

    boolean checkAuthUUID(UUID user);

    boolean checkAuthUsername(String name);

}
