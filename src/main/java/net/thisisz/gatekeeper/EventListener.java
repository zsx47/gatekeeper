package net.thisisz.gatekeeper;

import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.event.EventBus;
import me.lucko.luckperms.api.event.user.UserDataRecalculateEvent;
import me.lucko.luckperms.api.event.user.track.UserDemoteEvent;
import me.lucko.luckperms.api.event.user.track.UserPromoteEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.thisisz.gatekeeper.asynctask.CheckUserAuth;
import net.thisisz.gatekeeper.asynctask.SetGroupMember;
import net.thisisz.gatekeeper.asynctask.SetGroupNonMember;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements net.md_5.bungee.api.plugin.Listener {

    private GateKeeper plugin;
    private ServerInfo holdingServer, lobbyServer;

    public EventListener() {
        holdingServer = getPlugin().getProxy().getServerInfo(getPlugin().getConfiguration().getString("holding_server"));
        lobbyServer = getPlugin().getProxy().getServerInfo(getPlugin().getConfiguration().getString("lobby_server"));
        EventBus eventBus = GateKeeper.getPlugin().getLuckApi().getEventBus();
        eventBus.subscribe(UserDemoteEvent.class, this::onUserDemoteEvent);
        eventBus.subscribe(UserPromoteEvent.class, this::onUserPromoteEvent);
        eventBus.subscribe(UserDataRecalculateEvent.class, this::onUserDataRecalculateEvent);
    }

    public GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent event) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(),
                new CheckUserAuth(event.getPlayer(),
                        new SetGroupMember(event.getPlayer().getUniqueId()),
                        new SetGroupNonMember(event.getPlayer().getUniqueId())));
        if (!event.getPlayer().hasPermission("gatekeeper.join")) {
            event.getPlayer().connect(holdingServer);
        }
    }

    @EventHandler
    public void onServerKickEvent(ServerKickEvent event) {
        if (!event.getPlayer().hasPermission("gatekeeper.join")) {
            if (event.getKickedFrom() == holdingServer) {
                event.getPlayer().disconnect(new ComponentBuilder("Unable to find a server to put you in. Please try again later.").create());
            }
        }
    }

    @EventHandler
    public void onServerConnectEvent(ServerConnectEvent event) {
        if (event.getTarget() != holdingServer) {
            if (!event.getPlayer().hasPermission("gatekeeper.join")) {
                event.setCancelled(true);
                if (event.getPlayer().getServer() != null) {
                    if (event.getPlayer().getServer().getInfo() != holdingServer) {
                        event.getPlayer().connect(holdingServer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPermissionUpdateEvent(PermissionUpdateEvent event) {
        doUserPermUpdate(getPlugin().getLuckApi().getUser(getPlugin().getLuckApi().getUuidCache().getUUID(event.getPlayer().getUniqueId())));
    }

    private void onUserDemoteEvent(UserDemoteEvent event) {
        if (event.getUser() != null) {
            doUserPermUpdate(event.getUser());
        }
    }

    private void onUserPromoteEvent(UserPromoteEvent event) {
        if (event.getUser() != null) {
            doUserPermUpdate(event.getUser());
        }
    }

    private void onUserDataRecalculateEvent(UserDataRecalculateEvent event) {
        if (event.getUser() != null) {
            doUserPermUpdate(event.getUser());
        }
    }

    private void doUserPermUpdate(User user) {
        ProxiedPlayer player = getPlugin().getProxy().getPlayer(user.getUuid());
        doUserPermUpdate(player);
    }

    private void doUserPermUpdate(ProxiedPlayer player) {
        if (player != null) {
            if (player.hasPermission("gatekeeper.join")) {
                if (player.getServer().getInfo().getName().equals(holdingServer.getName())) {
                    player.connect(lobbyServer);
                }
            } else {
                if (!player.getServer().getInfo().getName().equals(holdingServer.getName())) {
                    player.connect(holdingServer);
                }
            }
        }
    }
}
