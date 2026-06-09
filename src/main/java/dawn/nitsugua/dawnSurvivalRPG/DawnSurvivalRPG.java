package dawn.nitsugua.dawnSurvivalRPG;

import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.ItemCommand;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.TestListeners.PlayerBreakingBlockListener;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InitialSetup;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DawnSurvivalRPG extends JavaPlugin {

    private static DawnSurvivalRPG instance;

    @Override
    public void onEnable() {
        instance = this;

        registerCommands();

        getServer().getPluginManager().registerEvents(new PlayerBreakingBlockListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        getLogger().info("Registering Commands...");
        LifecycleEventManager<Plugin> manager =this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            new ItemCommand(this).register(commands);
        });

        getLogger().info("Commands Registered!");

        getLogger().info("Registering Custom Items...");

    }

    @NotNull
    public static DawnSurvivalRPG getInstance() {return instance;}
}
