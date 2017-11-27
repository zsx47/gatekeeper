package net.thisisz.gatekeeper;

public class Config {

    private static GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    public static String getMemberGroup() {
        return GateKeeper.getPlugin().getConfiguration().getString("member_group");
    }

}
