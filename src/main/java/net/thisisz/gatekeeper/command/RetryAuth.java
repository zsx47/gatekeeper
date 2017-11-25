package net.thisisz.gatekeeper.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.asynctask.CheckUserAuth;
import net.thisisz.gatekeeper.asynctask.SetGroupMember;
import net.thisisz.gatekeeper.asynctask.SetGroupNonMember;

public class RetryAuth extends Command {

    public RetryAuth() {
        super("retrylogin", "gatekeeper.retry", "retryauth", "rl");
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            getPlugin().getProxy().getScheduler().runAsync(getPlugin(),
                    new CheckUserAuth((ProxiedPlayer) commandSender,
                            new SetGroupMember(((ProxiedPlayer) commandSender).getUniqueId()),
                            new SetGroupNonMember(((ProxiedPlayer) commandSender).getUniqueId())));
        }
    }

}
