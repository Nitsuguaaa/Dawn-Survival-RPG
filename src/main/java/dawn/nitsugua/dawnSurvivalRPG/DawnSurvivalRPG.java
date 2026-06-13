package dawn.nitsugua.dawnSurvivalRPG;

import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.ItemCommand;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemLoader;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.TestListeners.PlayerBreakingBlockListener;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InitialSetup;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DawnSurvivalRPG extends JavaPlugin {
    private static ComponentLogger log;
    private static DawnSurvivalRPG instance;

    @Override
    public void onEnable() {
        instance = this;
        log = ComponentLogger.logger("DawnRPG");

        log.info(TextUtil.format("<dark_gray>=============================================</dark_gray>"));
        log.info(TextUtil.format("     <green>Dawn Survival RPG</green> <dark_gray>v</dark_gray><white>" + getPluginMeta().getVersion() + "</white>"));

        InitialSetup.runFirstInitialize(instance);
        registerCommands();
        registerListeners();
        registerManagersAndLoaders();

        log.info(TextUtil.format("<dark_gray>=============================================</dark_gray>"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        log.info(TextUtil.format("<yellow>Registering Commands...</yellow>"));
        LifecycleEventManager<Plugin> manager =this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            new ItemCommand(this).register(commands);
        });

        log.info(TextUtil.format("<green>Commands Registered!</green>"));

    }

    private void registerListeners() {
        log.info(TextUtil.format("<yellow>Registering Listeners...</yellow>"));

        getServer().getPluginManager().registerEvents(new PlayerBreakingBlockListener(), this);

        log.info(TextUtil.format("<green>Listeners Registered!</green>"));
    }

    private void registerManagersAndLoaders() {
        log.info(TextUtil.format("<yellow>Registering Managers and Loaders...</yellow>"));
        CustomItemLoader.initialize(instance);
        log.info(TextUtil.format("<green>Managers and Loaders Registered!</green>"));
    }

    @NotNull
    public static DawnSurvivalRPG getInstance() {return instance;}

    @NotNull
    public ComponentLogger getLog() {return log;}
}
