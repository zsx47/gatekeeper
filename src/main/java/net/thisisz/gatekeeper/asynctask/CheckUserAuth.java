package net.thisisz.gatekeeper.asynctask;

import me.lucko.luckperms.api.User;
import net.thisisz.gatekeeper.Callback;
import net.thisisz.gatekeeper.GateKeeper;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.thisisz.gatekeeper.PermissionUpdateEvent;

public class CheckUserAuth implements Runnable {

    private ProxiedPlayer player;
    private Callback callbackTrue, callbackFalse;

    public CheckUserAuth(ProxiedPlayer player, Callback callbacktrue, Callback callbackfalse) {
        this.player = player;
        this.callbackTrue = callbacktrue;
        this.callbackFalse = callbackfalse;
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    @Override
    public void run() {
        User user = getPlugin().getLuckApi().getUser(player.getUniqueId());
        if (user.getPrimaryGroup().equals(getPlugin().getConfiguration().getString("default_group")) || user.getPrimaryGroup().equals(getPlugin().getConfiguration().getString("member_group"))) {
            if (getPlugin().getAuthModuleManager().runAuth(player)) {
                getPlugin().getLogger().info("Player was authenticated successfully. " + player.getUniqueId().toString() + " " + player.getName());
                callbackTrue.run();
                getPlugin().getProxy().getPluginManager().callEvent(new PermissionUpdateEvent(player));
            } else {
                getPlugin().getLogger().info("Player was not authenticated. " + player.getUniqueId().toString() + " " + player.getName());
                callbackFalse.run();
                getPlugin().getProxy().getPluginManager().callEvent(new PermissionUpdateEvent(player));
            }
        }
    }
}
