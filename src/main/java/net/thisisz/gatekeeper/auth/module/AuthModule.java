package net.thisisz.gatekeeper.auth.module;

import net.thisisz.gatekeeper.auth.AuthLevel;

import java.util.UUID;

public interface AuthModule {

    boolean checkAuthUUID(UUID user);

    boolean checkAuthUsername(String name);

    AuthLevel getAuthLevel();

}
