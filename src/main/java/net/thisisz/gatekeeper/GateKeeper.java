package net.thisisz.gatekeeper;

import me.lucko.luckperms.api.LuckPermsApi;
import net.thisisz.gatekeeper.auth.AuthModuleManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.thisisz.gatekeeper.command.RetryAuth;
import net.thisisz.gatekeeper.permissions.LuckPermsProvider;
import net.thisisz.gatekeeper.permissions.PermissionProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class GateKeeper extends Plugin {

    private Configuration configuration;
    private static GateKeeper instance;
    private AuthModuleManager authModuleManager;
    private static Config config;
    private PermissionProvider permissionHandler;
    private EventListener listener;

    @Override
    public void onEnable() {
        instance = this;

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

        listener = new EventListener();

        getProxy().getPluginManager().registerCommand(this, new RetryAuth());

        config = new Config(configuration);

        permissionHandler = new LuckPermsProvider();

        getLogger().info("Successfully loaded!");
    }

    public Boolean ReloadConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yaml"));
            config = new Config(configuration);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Config getConfig() { return config; };

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

    public PermissionProvider getPermissionHandler() { return permissionHandler; }

    public EventListener getListener() { return listener; }

}
