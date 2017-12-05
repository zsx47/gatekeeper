package net.thisisz.gatekeeper.asynctask;

import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.exceptions.ObjectAlreadyHasException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.thisisz.gatekeeper.Callback;
import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.PermissionUpdateEvent;

import java.awt.*;
import java.util.UUID;

public class SetGroupNonMember implements Callback, Runnable {


    private UUID uuid;

    public SetGroupNonMember(UUID uuid) {
        this.uuid = uuid;
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    @Override
    public void run() {
        UUID luckuuid = getPlugin().getLuckApi().getUuidCache().getUUID(uuid);
        User luckUser = getPlugin().getLuckApi().getUser(luckuuid);
        try {
            Node memberNode = getPlugin().getLuckApi().getNodeFactory().makeGroupNode(getPlugin().getLuckApi().getGroup(getPlugin().getConfiguration().getString("member_group"))).build();
            try {
                luckUser.unsetPermission(memberNode);
            } catch (Exception e) {
                if (getPlugin().debugMode()) {
                    getPlugin().getLogger().info(e.getMessage());
                    e.printStackTrace();
                }
            }
            Node defaultNode = getPlugin().getLuckApi().getNodeFactory().makeGroupNode(getPlugin().getLuckApi().getGroup(getPlugin().getConfiguration().getString("default_group"))).build();
            try {
                luckUser.setPermission(defaultNode);
            } catch (Exception e) {
                if (getPlugin().debugMode()) {
                    getPlugin().getLogger().info(e.getMessage());
                    e.printStackTrace();
                }
            }
            getPlugin().getLuckApi().getStorage().saveUser(luckUser)
                    .thenAcceptAsync(wasSuccessful -> {
                        if (!wasSuccessful) {
                            return;
                        }

                        System.out.println("Successfully set permission!");

                        // refresh the user's permissions, so the change is "live"
                        // this method is blocking, but it's fine, because this callback is
                        // ran async.
                        luckUser.refreshPermissions();
                        try {
                            luckUser.setPrimaryGroup(getPlugin().getConfiguration().getString("default_group"));
                        } catch (ObjectAlreadyHasException e) {
                        } catch (Exception e) {
                            getPlugin().getLogger().info(e.getMessage());
                            e.printStackTrace();
                        }
                    }, getPlugin().getLuckApi().getStorage().getAsyncExecutor());
        } catch (Exception e) {
            if (getPlugin().debugMode()) {
                getPlugin().getLogger().info(e.getMessage());
                e.printStackTrace();
            }
        }
        ProxiedPlayer p = getPlugin().getProxy().getPlayer(uuid);
        p.sendMessage(new ComponentBuilder(ChatColor.RED + getPlugin().getConfiguration().getString("fail_message")).create());
    }

}
