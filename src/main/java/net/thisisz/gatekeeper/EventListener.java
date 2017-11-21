package net.thisisz.gatekeeper;

import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.event.EventBus;
import me.lucko.luckperms.api.event.user.UserDataRecalculateEvent;
import me.lucko.luckperms.api.event.user.track.UserDemoteEvent;
import me.lucko.luckperms.api.event.user.track.UserPromoteEvent;
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
        if (!event.getPlayer().hasPermission("mcwpauth.join")) {
            if (holdingServer != null) {
                event.getPlayer().connect(holdingServer);
            } else {
                String servers = "";
                for (String server: getPlugin().getProxy().getServers().keySet()) {
                    servers = servers + server + ", ";
                }
                getPlugin().getLogger().info("Server not found! Valid server options:" + servers);
            }
        }
    }

    private void onUserDemoteEvent(UserDemoteEvent event) {
        doUserPermUpdate(event.getUser());
    }

    private void onUserPromoteEvent(UserPromoteEvent event) {
        doUserPermUpdate(event.getUser());
    }

    private void onUserDataRecalculateEvent(UserDataRecalculateEvent event) {
        if (event.getUser() != null) {
            doUserPermUpdate(event.getUser());
        }
    }

    private void doUserPermUpdate(User user) {
        ProxiedPlayer player = getPlugin().getProxy().getPlayer(user.getUuid());
        if (player != null) {
            if (user.hasPermission(getPlugin().getLuckApi().getNodeFactory().newBuilder("mcwpauth.join").build()).asBoolean()) {
                if (player.getServer().getInfo().getName().equals(getPlugin().getConfiguration().getString("holding_server"))) {
                    player.connect(getPlugin().getProxy().getServerInfo(getPlugin().getConfiguration().getString("lobby_server")));
                }
            } else {
                if (!player.getServer().getInfo().getName().equals(getPlugin().getConfiguration().getString("holding_server"))) {
                    player.connect(getPlugin().getProxy().getServerInfo(getPlugin().getConfiguration().getString("holding_server")));
                }
            }
        }
    }
}
