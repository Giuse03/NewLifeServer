package net.giuse.mainmodule;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.giuse.engine.ProcessEngine;
import net.giuse.engine.Worker;
import net.giuse.ezmessage.MessageBuilder;
import net.giuse.ezmessage.MessageLoader;
import net.giuse.mainmodule.commands.AbstractCommand;
import net.giuse.mainmodule.databases.ConnectorSQLite;
import net.giuse.mainmodule.files.FilesList;
import net.giuse.mainmodule.files.SQLFile;
import net.giuse.mainmodule.files.reflections.ReflectionsFiles;
import net.giuse.mainmodule.gui.GuiInitializer;
import net.giuse.mainmodule.message.MessageLoaderMain;
import net.giuse.mainmodule.services.Services;
import net.giuse.mainmodule.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.HashMap;

public class MainModule extends JavaPlugin {

    @Getter
    private final Injector injector = new InjectorBuilder().addDefaultHandlers("net.giuse").create();
    @Getter
    private ProcessEngine engine;
    @Getter
    private MessageBuilder messageBuilder;
    @Getter
    private final ConnectorSQLite connectorSQLite = new ConnectorSQLite();
    @Getter
    private MessageLoader messageLoader;
    private final Reflections reflections = new Reflections("net.giuse");
    private HashMap<Services, Integer> servicesByPriority = new HashMap<>();


    /*
     * Enable MainModule
     */
    @Override
    public void onEnable() {
        //Get current millis for check startup time
        long millis = System.currentTimeMillis();
        getLogger().info("┬žaLifeserver starting...");

        //declarations
        engine = new ProcessEngine(this);

        //setup
        setupInjector();
        setupMessage();
        setupFiles();
        setupSQL();

        //open connection
        connectorSQLite.openConnect();

        //another setup
        setupService();
        setupCommands();
        setupGuis();
        setupListeners();

        //close connection
        connectorSQLite.closeConnection();

        getLogger().info("┬žaLifeserver started in " + (System.currentTimeMillis() - millis) + "ms...");
    }

    /*
     * Disable MainModule
     */
    @Override
    public void onDisable() {
        //Unload services
        connectorSQLite.openConnect();
        servicesByPriority.keySet().forEach(Services::unload);
        connectorSQLite.closeConnection();
    }

    /*
     * Setup Files
     */
    private void setupFiles() {
        //Setup in default dir
        for (FilesList pathFile : FilesList.values()) {
            saveResource(pathFile.toString(), false);
        }
    }

    /*
     * Setup Injector
     */
    private void setupInjector() {
        injector.register(MainModule.class, this);
        injector.register(Worker.class, new Worker(engine));
    }

    /*
     * Setup Messages
     */
    private void setupMessage() {
        messageLoader = new MessageLoader(this, engine);
        messageBuilder = new MessageBuilder(messageLoader);
        injector.getSingleton(MessageLoaderMain.class).load();
    }

    /*
     * Setup services
     */
    private void setupService() {
        reflections.getSubTypesOf(Services.class).forEach(serviceKlass -> {
            Services services = injector.newInstance(serviceKlass);
            servicesByPriority.put(services, services.priority());
        });
        servicesByPriority = (HashMap<Services, Integer>) Utils.sortByValue(servicesByPriority);
        servicesByPriority.keySet().forEach(Services::load);
    }

    /*
     * Setup SQL
     */
    @SneakyThrows
    private void setupSQL() {
        ReflectionsFiles.loadFiles(new SQLFile());
    }

    /*
     * Setup Listeners
     */
    private void setupListeners() {
        reflections.getSubTypesOf(Listener.class).stream()
                .filter(listenerClass -> !listenerClass.getSimpleName().equalsIgnoreCase("FoodEvent")
                        && !listenerClass.getSimpleName().equalsIgnoreCase("EntityBackOnDeath"))
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(injector.getSingleton(listener), this));
    }

    private void setupGuis() {
        for (Class<? extends GuiInitializer> guiInitializer : reflections.getSubTypesOf(GuiInitializer.class)) {
            injector.getSingleton(guiInitializer).initInv();
        }
    }

    /*
     * Setup Commands
     */
    private void setupCommands() {
        for (Class<? extends AbstractCommand> command : reflections.getSubTypesOf(AbstractCommand.class)) {
            Utils.registerCommand(command.getName(), injector.getSingleton(command));
        }
    }

    /*
     * Get a Service by Class
     */
    public Services getService(Class<?> name) {
        return servicesByPriority.keySet().stream().filter(services -> services.getClass().equals(name)).findFirst().orElseThrow(() -> new NullPointerException("No Service Found"));
    }
}
