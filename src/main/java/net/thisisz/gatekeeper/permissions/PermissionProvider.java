package net.thisisz.gatekeeper.permissions;

import java.util.UUID;

public interface PermissionProvider {

    //All methods for permission handler should be run out of main thread!

    void setGroupMember(UUID uuid);

    void setGroupDefault(UUID uuid);

    String getPrimaryGroup(UUID uuid);

}
