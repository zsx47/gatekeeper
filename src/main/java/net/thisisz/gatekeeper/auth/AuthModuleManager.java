package net.thisisz.gatekeeper.auth;

import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.auth.module.AuthModule;
import net.thisisz.gatekeeper.auth.module.FlatFileModule;
import net.thisisz.gatekeeper.auth.module.HttpModule;
import net.thisisz.gatekeeper.auth.module.MysqlModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class AuthModuleManager {

    private static AuthModuleManager instance;
    private List<AuthModule> authModules = new ArrayList<AuthModule>();

    public AuthModuleManager() {
        instance = this;

    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    public static AuthModuleManager getManager() {
        return instance;
    }

    private void loadAuthModulesFromConfig() {
        Configuration authModules = getPlugin().getConfiguration().getSection("auth_modules");
        for (String key: authModules.getKeys()) {
            switch (key) {
                case "flatfile":
                    this.authModules.add(newFlatFileModule(authModules.getSection(key)));
                    break;
                case "mysql":
                    this.authModules.add(newMysqlModule(authModules.getSection(key)));
                    break;
                case "http":
                    this.authModules.add(newHttpModule(authModules.getSection(key)));
                    break;
                default:
                    getPlugin().getLogger().info("Unrecognized auth module type " + key);
                    break;
            }
        }
    }

    private FlatFileModule newFlatFileModule(Configuration section) {
        if (section.getKeys().contains("auth_level")) {
            return new FlatFileModule(section.getString("file"),
                    section.getBoolean("uuid_mode"),
                    AuthLevel.fromString(section.getString("auth_level")));
        }
        return new FlatFileModule(section.getString("file"),
                section.getBoolean("uuid_mode"));
    }

    private MysqlModule newMysqlModule(Configuration section) {
        if (section.getKeys().contains("auth_level")) {
            return new MysqlModule(section.getString("host"),
                    section.getString("port"),
                    section.getString("username"),
                    section.getString("password"),
                    section.getString("database"),
                    section.getString("table"),
                    section.getString("column"),
                    section.getBoolean("uuid_mode"),
                    AuthLevel.fromString(section.getString("auth_level")));
        }
        return new MysqlModule(section.getString("host"),
                section.getString("port"),
                section.getString("username"),
                section.getString("password"),
                section.getString("database"),
                section.getString("table"),
                section.getString("column"),
                section.getBoolean("uuid_mode"));
    }

    private HttpModule newHttpModule(Configuration section) {
        if (section.getKeys().contains("auth_level")) {
            return new HttpModule(section.getString("base_url"),
                    section.getString("method"),
                    section.getSection("other_parameters"),
                    section.getBoolean("uuid_mode"),
                    AuthLevel.fromString(section.getString("auth_level")));
        }
        return new HttpModule(section.getString("base_url"),
                section.getString("method"),
                section.getSection("other_parameters"),
                section.getBoolean("uuid_mode"));
    }

    public boolean runAuth(ProxiedPlayer player) {
        for (AuthModule module: authModules) {
            if (module.checkAuthUsername(player.getName()) || module.checkAuthUUID(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }


}
