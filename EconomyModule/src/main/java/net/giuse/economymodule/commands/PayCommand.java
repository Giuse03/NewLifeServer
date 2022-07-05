package net.giuse.economymodule.commands;

import net.giuse.economymodule.EconPlayer;
import net.giuse.economymodule.EconomyService;
import net.giuse.mainmodule.MainModule;
import net.giuse.mainmodule.commands.AbstractCommand;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class PayCommand extends AbstractCommand {
    private final EconomyService economyService;

    @Inject
    public PayCommand(final MainModule mainModule) {
        super("pay", "lifeserver.pay", true);
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
        if (args.length <= 1) {
            p.sendMessage(this.economyService.getMessage("economy-pay-args"));
        } else {
            if (!NumberUtils.isNumber(args[1])) {
                p.sendMessage(this.economyService.getMessage("economy-number"));
                return;
            }
            if (Double.parseDouble(args[1]) < 0.0) {
                p.sendMessage(this.economyService.getMessage("economy-number"));
                return;
            }
            if (this.economyService.getEconPlayer(args[0]) == null) {
                p.sendMessage(this.economyService.getMessage("economy-neverJoin"));
                return;
            }
            if (this.economyService.getCustomEcoManager().getBalance((OfflinePlayer) p) >= Double.parseDouble(args[1])) {
                final EconPlayer econPlayer = this.economyService.getEconPlayer(args[0]);
                this.economyService.getCustomEcoManager().depositPlayer(econPlayer.getPlayer(), Double.parseDouble(args[1]));
                this.economyService.getCustomEcoManager().withdrawPlayer((OfflinePlayer) p, Double.parseDouble(args[1]));
                p.sendMessage(this.economyService.getMessage("economy-pay-send").replace("%player%", econPlayer.getPlayer()).replace("%amount%", args[1]));
                Bukkit.getPlayer(econPlayer.getPlayer()).sendMessage(this.economyService.getMessage("economy-pay-receive").replace("%player%", p.getName()).replace("%amount%", args[1]));
            } else {
                p.sendMessage(this.economyService.getMessage("economy-no-money"));
            }
        }
    }
}