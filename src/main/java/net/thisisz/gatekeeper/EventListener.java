package net.thisisz.gatekeeper;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.thisisz.gatekeeper.event.PermissionUpdateEvent;
import net.thisisz.gatekeeper.permissions.PermissionProvider;

public class EventListener implements Listener {

    public EventListener() {
        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), this);
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    private PermissionProvider getPermissionHandler() { return getPlugin().getPermissionHandler(); }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent event) {
        checkAuth(event.getPlayer());
        if (!event.getPlayer().hasPermission("gatekeeper.join")) {
            event.getPlayer().connect(getPlugin().getConfig().getHoldingServer());
        }
    }

    public void checkAuth(ProxiedPlayer player) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> {
            String primaryGroup = getPermissionHandler().getPrimaryGroup(player.getUniqueId());
            if (primaryGroup.equals(getPlugin().getConfig().getDefaultGroup()) || primaryGroup.equals(getPlugin().getConfig().getMemberGroup())) {
                if (getPlugin().getAuthModuleManager().runAuth(player)) {
                    getPlugin().getLogger().info("Player was authenticated successfully. " + player.getUniqueId().toString() + " " + player.getName());
                    getPermissionHandler().setGroupMember(player.getUniqueId());
                } else {
                    player.sendMessage(new ComponentBuilder(getPlugin().getConfig().getAuthFailMessage()).create());
                    getPlugin().getLogger().info("Player was not authenticated. " + player.getUniqueId().toString() + " " + player.getName());
                    getPermissionHandler().setGroupDefault(player.getUniqueId());
                }
                doUserPermUpdate(player);
            }
        });
    }

    @EventHandler
    public void onServerKickEvent(ServerKickEvent event) {
        if (!event.getPlayer().hasPermission("gatekeeper.join")) {
            if (event.getKickedFrom() == getPlugin().getConfig().getHoldingServer()) {
                event.getPlayer().disconnect(new ComponentBuilder("Unable to find a server to put you in. Please try again later.").create());
            }
        }
    }

    @EventHandler
    public void onServerConnectEvent(ServerConnectEvent event) {
        if (!event.getPlayer().hasPermission("gatekeeper.join")) {
            if (event.getTarget() != getPlugin().getConfig().getHoldingServer()) {
                if (event.getPlayer().getServer() != null) {
                    if (event.getPlayer().getServer().getInfo() != getPlugin().getConfig().getHoldingServer()) {
                        event.getPlayer().connect(getPlugin().getConfig().getHoldingServer());
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    event.setTarget(getPlugin().getConfig().getHoldingServer());
                }
            }
        }
    }

    @EventHandler
    public void onPermissionUpdateEvent(PermissionUpdateEvent event) {
        doUserPermUpdate(event.getPlayer());
    }

    private void doUserPermUpdate(ProxiedPlayer player) {
        if (player != null) {
            if(player.getServer() != null) {
                if (player.hasPermission("gatekeeper.join")) {
                    if (player.getServer().getInfo().getName().equals(getPlugin().getConfig().getHoldingServer().getName())) {
                        player.connect(getPlugin().getConfig().getLobbyServer());
                    }
                } else {
                    if (!player.getServer().getInfo().getName().equals(getPlugin().getConfig().getHoldingServer().getName())) {
                        player.connect(getPlugin().getConfig().getHoldingServer());
                    }
                }
            }
        }
    }
}
