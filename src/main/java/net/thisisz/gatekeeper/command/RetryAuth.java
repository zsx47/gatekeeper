package net.thisisz.gatekeeper.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.gatekeeper.GateKeeper;

public class RetryAuth extends Command {

    public RetryAuth() {
        super("retrylogin", "gatekeeper.retry", "retryauth", "rl");
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        getPlugin().getListener().checkAuth((ProxiedPlayer) commandSender);
    }

}
