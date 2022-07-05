package net.giuse.simplycommandmodule.commands;


import net.giuse.mainmodule.MainModule;
import net.giuse.mainmodule.commands.AbstractCommand;
import net.giuse.simplycommandmodule.SimplyCommandService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class NearCommand extends AbstractCommand {
    private final SimplyCommandService simplyCommandService;

    @Inject
    public NearCommand(MainModule mainModule) {
        super("near", "lifeserver.near", false);
        simplyCommandService = (SimplyCommandService) mainModule.getService(SimplyCommandService.class);
        setNoPerm(simplyCommandService.getMex("no-perms"));

    }


    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            StringBuilder stringBuilder = new StringBuilder();
            Set<String> players = new HashSet<>();
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.getLocation().distance(player.getLocation()) <= 10 && !player.equals(other)) {
                    players.add(other.getName());
                }
            }

            if (players.isEmpty()) {
                player.sendMessage(simplyCommandService.getMex("near-nobody"));
            } else {
                players.forEach(i -> stringBuilder.append(i).append(","));
            }
            player.sendMessage(simplyCommandService.getMex("near").replace("%player_list%", stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString()));

        } else {
            commandSender.sendMessage(simplyCommandService.getMex("not-player"));

        }
    }
}