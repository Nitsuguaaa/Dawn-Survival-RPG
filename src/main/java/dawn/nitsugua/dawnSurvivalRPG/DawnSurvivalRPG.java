package dawn.nitsugua.dawnSurvivalRPG;

import dawn.nitsugua.dawnSurvivalRPG.API.DawnRPGAPI;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.DungeonCommand;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.ItemCommand;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.*;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Core.Tasks.DungeonTasks;
import dawn.nitsugua.dawnSurvivalRPG.Core.Tasks.ManaTasks;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemLoader;
import dawn.nitsugua.dawnSurvivalRPG.Items.Skills.SkillLoader;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.AntiEndGameListener;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.AntiMendingListener;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners.FancyNPCEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners.MobEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners.VillagerEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners.ItemEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners.PlayerEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.TestListeners.PlayerBreakingBlockListener;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMobLoader;
import dawn.nitsugua.dawnSurvivalRPG.Utils.*;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DawnSurvivalRPG extends JavaPlugin {
    private static ComponentLogger log;
    private static DawnSurvivalRPG instance;
    private GlobalTicker ticker;
    private DawnRPGAPI api;

    //managers
    private CooldownManager cooldownManager;
    private DungeonManager dungeonManager;
    private ManaManager manaManager;
    private MobManager mobManager;
    private PlayerManager playerManager;
    private SkillManager skillManager;
    private TaskManager taskManager;

    @Override
    public void onEnable() {
        instance = this;
        log = ComponentLogger.logger("DawnRPG");

        log.info(TextUtil.format("<dark_gray>=============================================</dark_gray>"));
        log.info(TextUtil.format("     <green>Dawn Survival RPG</green> <dark_gray>v</dark_gray><white>" + getPluginMeta().getVersion() + "</white>"));


        InitialSetup.runFirstInitialize(instance);
        registerManagersAndLoaders();

        registerCommands();
        registerListeners();

        ticker = new GlobalTicker(instance, taskManager);
        ticker.start();
        registerTasks();

        this.api = new DawnRPGAPI() {
            @Override
            public TaskManager getTaskManager() {
                return taskManager;
            }
        };

        Bukkit.getServicesManager().register(
                DawnRPGAPI.class,
                this.api,
                this,
                ServicePriority.Normal
        );

        log.info(TextUtil.format("<dark_gray>=============================================</dark_gray>"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(ticker != null) {
            ticker.stop();
        }
    }

    private void registerCommands() {
        log.info(TextUtil.format("<yellow>Registering Commands...</yellow>"));
        LifecycleEventManager<Plugin> manager =this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            new ItemCommand(instance).register(commands);
            new DungeonCommand(instance).register(commands  );
        });

        log.info(TextUtil.format("<green>Commands Registered!</green>"));

    }

    private void registerListeners() {
        log.info(TextUtil.format("<yellow>Registering Listeners...</yellow>"));

        getServer().getPluginManager().registerEvents(new PlayerBreakingBlockListener(), instance);
        getServer().getPluginManager().registerEvents(new ItemEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new PlayerEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new MobEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new VillagerEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new FancyNPCEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new AntiMendingListener(), instance);
        getServer().getPluginManager().registerEvents(new AntiEndGameListener(), instance);

        log.info(TextUtil.format("<green>Listeners Registered!</green>"));
    }

    private void registerManagersAndLoaders() {
        log.info(TextUtil.format("<yellow>Registering Managers and Loaders...</yellow>"));
        DawnKeys.init(instance);
        CustomItemLoader.initialize(instance);
        SkillLoader.initialize(instance);
        CustomMobLoader.initialize(instance);

        cooldownManager = new CooldownManager(instance);
        dungeonManager = new DungeonManager(instance);
        manaManager = new ManaManager(instance);
        mobManager  = new MobManager(instance);
        playerManager = new PlayerManager(instance, getManaManager());
        skillManager = new SkillManager(instance, getManaManager());
        taskManager = new TaskManager();

        log.info(TextUtil.format("<green>Managers and Loaders Registered!</green>"));
    }

    private void registerTasks() {
        log.info(TextUtil.format("<yellow>Tasks...</yellow>"));
        ManaTasks.initManaTasks(instance);
        DungeonTasks.runDungeonMobSpawning(instance);
        log.info(TextUtil.format("<green>Tasks Registered!</green>"));
    }

    @NotNull
    public static DawnSurvivalRPG getInstance() {return instance;}

    @NotNull
    public DawnRPGAPI getAPI () {
        return api;
    }


    //public CustomItemManager getCustomItemManager() {return customItemManager;}
    public CooldownManager getCooldownManager() {return cooldownManager;}
    public DungeonManager getDungeonManager() {return dungeonManager;}
    public MobManager getMobManager() {return mobManager;}
    public ManaManager getManaManager() {return manaManager;}
    public PlayerManager getPlayerManager() {return playerManager;}
    public SkillManager getSkillManager() {return skillManager;}
    public TaskManager getTaskManager() {return taskManager;}

    @NotNull
    public ComponentLogger getLog() {return log;}
}
