package dawn.nitsugua.dawnSurvivalRPG;

import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.ItemCommand;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemLoader;
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

        getLogger().info("=============================================");
        getLogger().info("     Dawn Survival RPG v" + getPluginMeta().getVersion());

        InitialSetup.runFirstInitialize(instance);
        registerCommands();
        registerListeners();
        registerItems();

        getLogger().info("=============================================");
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

    }

    private void registerListeners() {
        getLogger().info("Registering Listeners...");

        getServer().getPluginManager().registerEvents(new PlayerBreakingBlockListener(), this);

        getLogger().info("Listeners Registered!");
    }

    private void registerItems() {
        getLogger().info("Registering Custom Items...");
        CustomItemLoader.initialize(instance);
        getLogger().info("Custom Items Registered!");
    }

    @NotNull
    public static DawnSurvivalRPG getInstance() {return instance;}
}
