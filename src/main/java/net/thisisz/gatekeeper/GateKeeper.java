package net.thisisz.gatekeeper;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.thisisz.gatekeeper.auth.AuthModuleManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.thisisz.gatekeeper.command.RetryAuth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

public class GateKeeper extends Plugin {

    private Configuration configuration;
    private static GateKeeper instance;
    private LuckPermsApi luckApi;
    private AuthModuleManager authModuleManager;

    @Override
    public void onEnable() {
        instance = this;

        Optional<LuckPermsApi> api = LuckPerms.getApiSafe();
        if (api.isPresent()) {
            this.luckApi = api.get();
            getLogger().info("Luck perms api loaded.");
        } else {
            getLogger().warning("Failed to load Luck Perms api.");
        }

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yaml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yaml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yaml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLogger().info("Loading auth modules.");
        authModuleManager = new AuthModuleManager();

        getProxy().getPluginManager().registerListener(this, new EventListener());

        getProxy().getPluginManager().registerCommand(this, new RetryAuth());

        getLogger().info("Successfully loaded!");
    }

    public AuthModuleManager getAuthModuleManager() {
        return authModuleManager;
    }

    public boolean debugMode() {
        return getConfiguration().getBoolean("debug_mode");
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static GateKeeper getPlugin() {
        return instance;
    }

    public LuckPermsApi getLuckApi() {
        return luckApi;
    }
}
