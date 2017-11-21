package net.thisisz.gatekeeper.asynctask;

import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.exceptions.ObjectAlreadyHasException;
import net.thisisz.gatekeeper.Callback;
import net.thisisz.gatekeeper.GateKeeper;

import java.util.UUID;

public class SetGroupMember implements Callback, Runnable {


    private final UUID uuid;

    public SetGroupMember(UUID uuid) {
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
            Node defaultNode = getPlugin().getLuckApi().getNodeFactory().makeGroupNode(getPlugin().getLuckApi().getGroup(getPlugin().getConfiguration().getString("default_group"))).build();
            try {
                luckUser.unsetPermission(defaultNode);
            } catch (Exception e) {
                getPlugin().getLogger().info("User doesn't have default group.");
            }
            Node memberNode = getPlugin().getLuckApi().getNodeFactory().makeGroupNode(getPlugin().getLuckApi().getGroup(getPlugin().getConfiguration().getString("member_group"))).build();
            luckUser.setPermission(memberNode);
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
                            luckUser.setPrimaryGroup(getPlugin().getConfiguration().getString("member_group"));
                        } catch (ObjectAlreadyHasException e) {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }, getPlugin().getLuckApi().getStorage().getAsyncExecutor());
        } catch (ObjectAlreadyHasException e) {
            getPlugin().getLogger().info("User is already a part of a group.");
        }
    }

}