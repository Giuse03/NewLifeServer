package net.giuse.teleportmodule.commands.home;

import net.giuse.ezmessage.MessageBuilder;
import net.giuse.mainmodule.MainModule;
import net.giuse.mainmodule.commands.AbstractCommand;
import net.giuse.teleportmodule.TeleportModule;
import net.giuse.teleportmodule.subservice.HomeLoaderService;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class DelHomeCommand extends AbstractCommand {
    private final HomeLoaderService homeLoaderService;
    private final MessageBuilder messageBuilder;

    @Inject
    public DelHomeCommand(MainModule mainModule) {
        super("delhome", "lifeserver.delhome", true);
        homeLoaderService = (HomeLoaderService) mainModule.getService(HomeLoaderService.class);
        messageBuilder = mainModule.getMessageBuilder();
        setNoPerm("No perms");
        
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        //Check if sender is Console
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Not Supported From Console");
            return;
        }
        Player sender = (Player) commandSender;

        //Check if player has home
        if (homeLoaderService.getCacheHome().estimatedSize() == 0) {
            messageBuilder.setCommandSender(sender).setIDMessage("no_home_found").sendMessage();
            return;
        }

        //Check if player has multiple home
        if (sender.hasPermission("lifeserver.delhome.multiple") || sender.isOp()) {
            if (args.length == 0) {

                //Check if player has one home
                if (homeLoaderService.getHome(sender.getUniqueId()).estimatedSize() == 1) {
                    homeLoaderService.getHome(sender.getUniqueId()).asMap().keySet().forEach(home -> homeLoaderService.getHome(sender.getUniqueId()).invalidate(home));
                    messageBuilder.setCommandSender(sender).setIDMessage("deleted_home").sendMessage();
                    return;
                }
                messageBuilder.setCommandSender(sender).setIDMessage("select-home").sendMessage();
            }

            //Check if home exists
            if (homeLoaderService.getHome(sender.getUniqueId()).getIfPresent(args[0]) == null) {
                messageBuilder.setCommandSender(sender).setIDMessage("no_home_found").sendMessage();
                return;
            }

            //Delete home
            homeLoaderService.getHome(sender.getUniqueId()).invalidate(args[0].toLowerCase());
            messageBuilder.setCommandSender(sender).setIDMessage("deleted_home").sendMessage();
            return;
        }

        //Delete Home
        homeLoaderService.getHome(sender.getUniqueId()).asMap().keySet().forEach(home -> homeLoaderService.getHome(sender.getUniqueId()).invalidate(home));
        messageBuilder.setCommandSender(sender).setIDMessage("deleted_home").sendMessage();
    }
}
