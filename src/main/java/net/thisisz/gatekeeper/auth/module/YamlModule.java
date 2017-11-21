package net.thisisz.gatekeeper.auth.module;

import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.auth.AuthLevel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public class YamlModule implements AuthModule {


    private File file;
    private String filename;
    private AuthLevel authLevel;
    private boolean uuidMode;

    public YamlModule(String filename, boolean uuid) {
        this.authLevel = AuthLevel.NORMAL;
        this.filename = filename;
        this.uuidMode = uuid;
        file = new File(getPlugin().getDataFolder(), this.filename);
        initFile();
    }

    public YamlModule(String filename, boolean uuid, AuthLevel authLevel) {
        this.authLevel = authLevel;
        this.filename = filename;
        this.uuidMode = uuid;
        file = new File(getPlugin().getDataFolder(), this.filename);
        initFile();
    }

    private void initFile() {
        if (!file.exists()) {
            try (InputStream in = getPlugin().getResourceAsStream("whitelist.yaml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    public AuthLevel getAuthLevel() {
        return authLevel;
    }

    @Override
    public boolean checkAuthUUID(UUID user) {
        return false;
    }

    @Override
    public boolean checkAuthUsername(String name) {
        return false;
    }
}
