package net.thisisz.gatekeeper.auth;

import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.asynctask.SendDeprecatedAuthMethod;
import net.thisisz.gatekeeper.auth.module.AuthModule;
import net.thisisz.gatekeeper.auth.module.YamlModule;
import net.thisisz.gatekeeper.auth.module.HttpModule;
import net.thisisz.gatekeeper.auth.module.MysqlModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthModuleManager {

    private static AuthModuleManager instance;
    private AuthLevel maxAuthLevel = AuthLevel.NONE;
    private List<AuthModule> authModules = new ArrayList<AuthModule>();

    public AuthModuleManager() {
        instance = this;
        loadAuthModulesFromConfig();
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    public static AuthModuleManager getManager() {
        return instance;
    }

    private void loadAuthModulesFromConfig() {
        Configuration authModules = getPlugin().getConfiguration().getSection("auth_modules");
        AuthModule module;
        for (String key: authModules.getKeys()) {
            switch (key) {
                case "yaml":
                    module = newYamlModule(authModules.getSection(key));
                    this.authModules.add(module);
                    getPlugin().getLogger().info("Loaded yaml module. AuthLevel: " + module.getAuthLevel().toString() );
                    break;
                case "mysql":
                    module = newMysqlModule(authModules.getSection(key));
                    this.authModules.add(module);
                    getPlugin().getLogger().info("Loaded mysql module. AuthLevel: " + module.getAuthLevel().toString() );
                    break;
                case "http":
                    module = newHttpModule(authModules.getSection(key));
                    this.authModules.add(module);
                    getPlugin().getLogger().info("Loaded http module. AuthLevel: " + module.getAuthLevel().toString() );
                    break;
                default:
                    getPlugin().getLogger().info("Unrecognized auth module type " + key);
                    break;
            }
        }
        updateMaxAuthLevel();
    }

    private void updateMaxAuthLevel() {
        for (AuthModule module: authModules) {
            if (module.getAuthLevel().asInt() > maxAuthLevel.asInt()) {
                maxAuthLevel = module.getAuthLevel();
            }
        }
    }

    private YamlModule newYamlModule(Configuration section) {
        boolean uuidMode = getUuidMode(section);
        if (section.getKeys().contains("auth_level")) {
            return new YamlModule(section.getString("file"),
                    uuidMode,
                    AuthLevel.fromString(section.getString("auth_level")));
        }
        return new YamlModule(section.getString("file"),
                uuidMode);
    }

    private MysqlModule newMysqlModule(Configuration section) {
        boolean uuidMode = getUuidMode(section);
        if (section.getKeys().contains("auth_level")) {
            return new MysqlModule(section.getString("host"),
                    section.getString("port"),
                    section.getString("username"),
                    section.getString("password"),
                    section.getString("database"),
                    section.getString("table"),
                    section.getString("column"),
                    uuidMode,
                    AuthLevel.fromString(section.getString("auth_level")));
        }
        return new MysqlModule(section.getString("host"),
                section.getString("port"),
                section.getString("username"),
                section.getString("password"),
                section.getString("database"),
                section.getString("table"),
                section.getString("column"),
                uuidMode);
    }

    private HttpModule newHttpModule(Configuration section) {
        boolean uuidMode = getUuidMode(section);
        if (section.getKeys().contains("auth_level")) {
            return new HttpModule(section.getString("base_url"),
                    section.getString("method"),
                    section.getSection("other_parameters"),
                    uuidMode,
                    AuthLevel.fromString(section.getString("auth_level")));
        }
        return new HttpModule(section.getString("base_url"),
                section.getString("method"),
                section.getSection("other_parameters"),
                uuidMode);
    }

    public boolean runAuth(ProxiedPlayer player) {
        AuthLevel authLevel = AuthLevel.NONE;
        for (AuthModule module: authModules) {
            if (module.checkAuthUsername(player.getName()) || module.checkAuthUUID(player.getUniqueId())) {
                if (authLevel.asInt() < module.getAuthLevel().asInt()) {
                    authLevel = module.getAuthLevel();
                    if (authLevel == maxAuthLevel) {
                        return true;
                    }
                }
            }
        }
        if (authLevel.asInt() > AuthLevel.NONE.asInt()) {
            if (Objects.equals(authLevel.asInt(), AuthLevel.DEPRECATED.asInt())) {
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new SendDeprecatedAuthMethod(player));
            }
            return true;
        }

        return false;
    }

    private boolean getUuidMode(Configuration section) {
        if (section.getKeys().contains("uuid_mode")) {
            return section.getBoolean("uuid_mode");
        } else {
            return false;
        }
    }


}
