package net.giuse.mainmodule.commands;

import lombok.Getter;
import lombok.Setter;
import net.giuse.engine.ProcessEngine;
import net.giuse.engine.Worker;
import net.giuse.mainmodule.MainModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;


/**
 * AbstractCommand for create Concrete Commands
 */

@Getter
public abstract class AbstractCommand extends Command {
    private final String permission;
    private final boolean async;
    @Inject
    private Worker worker;
    @Inject
    private MainModule mainModule;
    public AbstractCommand(String name, String permission,boolean async) {
        super(name);
        this.permission = permission;
        this.async = async;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (!sender.hasPermission(this.permission)) {
            mainModule.getMessageBuilder().setCommandSender(sender).setIDMessage("no-perms").sendMessage();
            return true;
        }
        worker.executeProcess(CompletableFuture.supplyAsync(() -> () -> execute(sender, args)), async);
        return true;
    }

    public abstract void execute(CommandSender commandSender, String[] args);


}