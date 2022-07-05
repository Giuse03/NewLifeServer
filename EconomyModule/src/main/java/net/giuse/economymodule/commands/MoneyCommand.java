package net.giuse.economymodule.commands;

import net.giuse.economymodule.EconomyService;
import net.giuse.mainmodule.MainModule;
import net.giuse.mainmodule.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class MoneyCommand extends AbstractCommand {
    private final EconomyService economyService;

    @Inject
    public MoneyCommand(final MainModule mainModule) {
        super("money", "lifeserver.money", true);
        this.economyService = (EconomyService) mainModule.getService(EconomyService.class);
        this.setNoPerm(this.economyService.getMessage("no-perms"));
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Not supported from console");
            return;
        }
        final Player p = (Player) commandSender;
        if (args.length == 0) {
            p.sendMessage(this.economyService.getMessage("economy-balance").replace("%money%", String.valueOf(this.economyService.getEconPlayer(p.getName()).getBalance())));
        } else if (p.hasPermission("lifeserver.balance.other")) {
            if (this.economyService.getEconPlayer(args[0]) != null) {
                p.sendMessage(this.economyService.getMessage("economy-balance-other").replace("%money%", String.valueOf(this.economyService.getEconPlayer(p.getName()).getBalance())).replace("%player%", args[1]));
            } else {
                p.sendMessage(this.economyService.getMessage("economy-neverJoin"));
            }
        } else {
            p.sendMessage(this.economyService.getMessage("no-perms"));
        }
    }
}