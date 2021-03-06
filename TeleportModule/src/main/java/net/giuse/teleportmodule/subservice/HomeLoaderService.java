package net.giuse.teleportmodule.subservice;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import lombok.Getter;
import lombok.SneakyThrows;
import net.giuse.mainmodule.MainModule;
import net.giuse.mainmodule.serializer.Serializer;
import net.giuse.mainmodule.services.Services;
import net.giuse.teleportmodule.database.homequery.HomeQuery;
import net.giuse.teleportmodule.database.homequery.SaveQueryHome;
import net.giuse.teleportmodule.serializer.HomeBuilderSerializer;
import net.giuse.teleportmodule.serializer.serializedobject.HomeSerialized;
import org.bukkit.Location;

import javax.inject.Inject;
import java.util.UUID;

public class HomeLoaderService extends Services {
    @Getter
    private Object2ObjectMap<UUID, Object2ObjectMap<String, Location>> cacheHome;

    @Getter
    private Serializer<HomeSerialized> homeBuilderSerializer;
    @Inject
    private MainModule mainModule;

    /*
     * Load Service
     */
    @Override
    @SneakyThrows
    public void load() {
        mainModule.getLogger().info("§8[§2Life§aServer §7>> §eTeleportModule§9] §7Loading Home...");
        homeBuilderSerializer = mainModule.getInjector().getSingleton(HomeBuilderSerializer.class);
        cacheHome = new Object2ObjectArrayMap<>();

        //Load Home
        mainModule.getInjector().getSingleton(HomeQuery.class).query();
    }

    /*
     * Unload Service
     */
    @Override
    public void unload() {
        mainModule.getLogger().info("§8[§2Life§aServer §7>> §eTeleportModule§9] §7Unloading Home...");
        mainModule.getInjector().getSingleton(SaveQueryHome.class).query();

    }

    /*
     * Get Service Priority
     */
    @Override
    public int priority() {
        return 1;
    }

    /*
     * Get Home from player's UUID
     */
    public Object2ObjectMap<String, Location> getHome(UUID owner) {
        return cacheHome.get(owner);
    }
}
