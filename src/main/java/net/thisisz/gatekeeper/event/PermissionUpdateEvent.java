package net.thisisz.gatekeeper.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PermissionUpdateEvent extends Event {

    private ProxiedPlayer player;

    public PermissionUpdateEvent(ProxiedPlayer player) {
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }


}
