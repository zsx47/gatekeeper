package net.thisisz.gatekeeper;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

public class Config {

    private final Configuration config;

    public Config(Configuration config) { this.config = config; }

    private static GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    public String getMemberGroup() { return config.getString("member_group"); }

    public String getDefaultGroup() {
        return config.getString("default_group");
    }

    public ServerInfo getHoldingServer() { return getPlugin().getProxy().getServerInfo(config.getString("holding_server")); }

    public ServerInfo getLobbyServer() { return getPlugin().getProxy().getServerInfo(config.getString("lobby_server")); }

    public String getAuthFailMessage() { return config.getString("fail_message"); }

    public boolean isDebugMode() { return config.getBoolean("debug_mode"); }

}
