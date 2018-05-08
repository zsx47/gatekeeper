package net.thisisz.gatekeeper.permissions;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.DataMutateResult;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.event.EventBus;
import me.lucko.luckperms.api.event.LuckPermsEvent;
import me.lucko.luckperms.api.event.user.UserDataRecalculateEvent;
import me.lucko.luckperms.api.event.user.track.UserDemoteEvent;
import me.lucko.luckperms.api.event.user.track.UserPromoteEvent;
import me.lucko.luckperms.api.manager.UserManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.event.PermissionUpdateEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LuckPermsProvider implements PermissionProvider, Listener {

    private LuckPermsApi luckApi;

    public LuckPermsProvider() {
        Optional<LuckPermsApi> api = LuckPerms.getApiSafe();
        if (api.isPresent()) {
            this.luckApi = api.get();
            getPlugin().getLogger().info("Luck perms api loaded.");
        } else {
            getPlugin().getLogger().warning("Failed to load Luck Perms api.");
        }
        EventBus eventBus = luckApi.getEventBus();
        eventBus.subscribe(UserDemoteEvent.class, this::onUserDemoteEvent);
        eventBus.subscribe(UserPromoteEvent.class, this::onUserPromoteEvent);
        eventBus.subscribe(UserDataRecalculateEvent.class, this::onUserDataRecalculateEvent);
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    private Node getMemberGroupNode() {
        return luckApi.getNodeFactory().makeGroupNode(getPlugin().getConfig().getMemberGroup()).build();
    }

    private Node getDefaultGroupNode() {
        return luckApi.getNodeFactory().makeGroupNode(getPlugin().getConfig().getDefaultGroup()).build();
    }

    private void handleException(Exception e) {
        getPlugin().getLogger().warning(e.getMessage());
        e.printStackTrace();
    }

    private User getUser(UUID uuid) throws ExecutionException, InterruptedException {
        UserManager userManager = luckApi.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(uuid);
        return userFuture.get();
    }

    private void onUserDemoteEvent(UserDemoteEvent event) {
        doUserPermUpdate(event.getUser());
    }

    private void onUserPromoteEvent(UserPromoteEvent event) {
        doUserPermUpdate(event.getUser());
    }

    private void onUserDataRecalculateEvent(UserDataRecalculateEvent event) {
        doUserPermUpdate(event.getUser());
    }

    private void doUserPermUpdate(User user) {
        ProxiedPlayer player = getPlugin().getProxy().getPlayer(user.getUuid());
        if (player != null) {
            getPlugin().getProxy().getPluginManager().callEvent(new PermissionUpdateEvent(player));
        }
    }

    @Override
    public void setGroupMember(UUID uuid) {
        try {
            User user = getUser(uuid);
            user.unsetPermission(getDefaultGroupNode());
            user.setPermission(getMemberGroupNode());
            user.setPrimaryGroup(getPlugin().getConfig().getMemberGroup());
            luckApi.getUserManager().saveUser(user).thenRunAsync(user::refreshCachedData);
        } catch (Exception e) {
            handleException(e);
        }

    }

    @Override
    public void setGroupDefault(UUID uuid) {
        try {
            User user = getUser(uuid);
            user.unsetPermission(getMemberGroupNode());
            user.setPermission(getDefaultGroupNode());
            user.setPrimaryGroup(getPlugin().getConfig().getDefaultGroup());
            luckApi.getUserManager().saveUser(user).thenRunAsync(user::refreshCachedData);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getPrimaryGroup(UUID uuid) {
        try {
            User user = getUser(uuid);
            return user.getPrimaryGroup();
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }


}
