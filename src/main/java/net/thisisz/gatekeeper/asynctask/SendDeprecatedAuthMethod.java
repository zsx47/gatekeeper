package net.thisisz.gatekeeper.asynctask;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.thisisz.gatekeeper.GateKeeper;

public class SendDeprecatedAuthMethod implements Runnable {

    private ProxiedPlayer player;

    public SendDeprecatedAuthMethod(ProxiedPlayer player) {
        this.player = player;
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    @Override
    public void run() {
        player.sendMessage(new ComponentBuilder(getPlugin().getConfiguration().getString("deprecated_method_message")).create());
    }

}
