package net.giuse.simplycommandmodule.commands;

import net.giuse.mainmodule.MainModule;
import net.giuse.mainmodule.commands.AbstractCommand;
import net.giuse.simplycommandmodule.SimplyCommandService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class GodCommand extends AbstractCommand {
    private final SimplyCommandService simplyCommandService;

    @Inject
    public GodCommand(MainModule mainModule) {
        super("god", "lifeserver.god", true);
        simplyCommandService = (SimplyCommandService) mainModule.getService(SimplyCommandService.class);
        setNoPerm(simplyCommandService.getMex("no-perms"));

    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            if (commandSender instanceof ConsoleCommandSender) {
                commandSender.sendMessage(simplyCommandService.getMex("not-player"));
                return;
            }

            Player player = (Player) commandSender;
            if (simplyCommandService.getStringsNameGods().contains(player.getName())) {
                player.sendMessage(simplyCommandService.getMex("god-disabled"));
                simplyCommandService.getStringsNameGods().remove(player.getName());
                return;
            }

            player.sendMessage(simplyCommandService.getMex("god-enabled"));
            simplyCommandService.getStringsNameGods().add(player.getName());
            return;


        }

        if (!commandSender.hasPermission("lifeserver.god.other")) {
            commandSender.sendMessage(simplyCommandService.getMex("no-perms"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            commandSender.sendMessage(simplyCommandService.getMex("player-not-online"));
            return;
        }

        if (simplyCommandService.getStringsNameGods().contains(target.getName())) {
            target.sendMessage(simplyCommandService.getMex("god-disabled"));
            commandSender.sendMessage(simplyCommandService.getMex("god-other-disabled").replace("%player_name%", target.getName()));
            simplyCommandService.getStringsNameGods().remove(target.getName());
            return;
        }

        target.sendMessage(simplyCommandService.getMex("god-enabled"));
        commandSender.sendMessage(simplyCommandService.getMex("god-other-enabled").replace("%player_name%", target.getName()));
        simplyCommandService.getStringsNameGods().add(target.getName());
    }


}
