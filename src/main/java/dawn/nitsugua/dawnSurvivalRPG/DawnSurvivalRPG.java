package dawn.nitsugua.dawnSurvivalRPG;

import dawn.nitsugua.dawnSurvivalRPG.API.DawnRPGAPI;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.*;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.*;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Core.Tasks.*;
import dawn.nitsugua.dawnSurvivalRPG.Database.BankDAO;
import dawn.nitsugua.dawnSurvivalRPG.Database.PlayerDAO;
import dawn.nitsugua.dawnSurvivalRPG.Database.ProtectedChunkDAO;
import dawn.nitsugua.dawnSurvivalRPG.Database.TrackedChestDAO;
import dawn.nitsugua.dawnSurvivalRPG.Economy.SellLoader;
import dawn.nitsugua.dawnSurvivalRPG.ExcellentEconomy.DawnExcellentEconomy;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemFactory;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemLoader;
import dawn.nitsugua.dawnSurvivalRPG.Items.Skills.SkillLoader;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.AntiEndGameListener;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.AntiMendingListener;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners.FancyNPCEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners.MobEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners.VillagerEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.GuiListeners.GuiEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners.ItemEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners.PlayerEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners.PlayerPartyEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.StructureListeners.LootGenerateEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.StructureListeners.StructureEvents;
import dawn.nitsugua.dawnSurvivalRPG.Listeners.TestListeners.PlayerBreakingBlockListener;
import dawn.nitsugua.dawnSurvivalRPG.Loots.LootTableLoader;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMobLoader;
import dawn.nitsugua.dawnSurvivalRPG.Utils.*;
import dawn.nitsugua.dawnSurvivalRPG.placeholders.DawnBankPlaceholder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

import java.util.List;

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
    private CustomItemFactory customItemFactory;
    private LootManager lootManager;
    private CustomRecipeManager customRecipeManager;
    private GuiManager guiManager;
    private CustomItemManager customItemManager;
    private OverworldManager overworldManager;
    private VaultAndSpawnerManager vaultAndSpawnerManager;
    private NetheriteSmithingManager netheriteSmithingManager;
    private PartyManager partyManager;

    //loaders with managers
    private SellLoader sellLoader;

    //External plugins
    private DawnExcellentEconomy economyAPI;

    //Databases
    private DatabaseManager databaseManager;
    private BankDAO bankDAO;
    private ProtectedChunkDAO chunkDAO;
    private TrackedChestDAO chestDAO;
    private PlayerDAO playerDAO;

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

        if(databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    private void registerCommands() {
        log.info(TextUtil.format("<yellow>Registering Commands...</yellow>"));
        LifecycleEventManager<Plugin> manager =this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            new ItemCommand(instance).register(commands);
            new DungeonCommand(instance).register(commands);
            new MenuCommand(instance).register(commands);
            new BankCommand(instance).register(commands);
            new SellCommand(instance).register(commands);
            new MobCommand(instance).register(commands);
            new LootCommand(instance).register(commands);
            new GuiTesterCommand(instance).register(commands);
            new RecipeCommand(instance).register(commands);
            new ChunkCommand(instance).register(commands);
            new PartyCommand(instance).register(commands);
        });

        log.info(TextUtil.format("<green>Commands Registered!</green>"));

    }

    private void registerListeners() {
        log.info(TextUtil.format("<yellow>Registering Listeners...</yellow>"));

        //getServer().getPluginManager().registerEvents(new PlayerBreakingBlockListener(instance), instance);
        getServer().getPluginManager().registerEvents(new ItemEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new PlayerEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new MobEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new VillagerEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new AntiMendingListener(instance), instance);
        getServer().getPluginManager().registerEvents(new AntiEndGameListener(), instance);
        getServer().getPluginManager().registerEvents(new GuiEvents(), instance);
        getServer().getPluginManager().registerEvents(new StructureEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new LootGenerateEvents(instance), instance);
        getServer().getPluginManager().registerEvents(new PlayerPartyEvents(instance), instance);

        Bukkit.getScheduler().runTaskLater(this, this::hookOptionalPlugins, 1L);

        log.info(TextUtil.format("<green>Listeners Registered!</green>"));
    }

    private void registerManagersAndLoaders() {
        log.info(TextUtil.format("<yellow>Registering Managers and Loaders...</yellow>"));

        databaseManager = new DatabaseManager(instance);
        databaseManager.initialize();
        bankDAO = new BankDAO(instance);
        chunkDAO = new ProtectedChunkDAO(instance);
        chestDAO = new TrackedChestDAO(instance);
        playerDAO = new PlayerDAO(instance);

        try{
            List<ProtectedChunkDAO.ProtectedChunkData> loadedChunks = chunkDAO.loadAllChunksAsync().get();

            for(ProtectedChunkDAO.ProtectedChunkData data : loadedChunks) {
                DawnRegistry.getInstance().protectChunk(data.worldName(), data.chunkX(), data.chunkZ(), data.minY(), data.maxY(), data.structureKey());
            }

            getLog().info(TextUtil.sendLogInfo("ProtectedChunks", TextUtil.LogInfo.SUCCESS, "Loaded " + loadedChunks.size() + " protected chunks from database."));
        } catch (Exception e) {
            getLogger().severe("CRITICAL: Failed to load protected chunks on startup! " + e.getMessage());
        }

        DawnKeys.init(instance);
        CustomItemLoader.initialize(instance);
        SkillLoader.initialize(instance);
        CustomMobLoader.initialize(instance);
        LootTableLoader.initialize(instance);
        OverworldManager.loadStructureYRanges(instance);

        customItemManager = new CustomItemManager(instance);
        customItemFactory = new CustomItemFactory(instance);
        cooldownManager = new CooldownManager(instance);
        dungeonManager = new DungeonManager(instance);
        manaManager = new ManaManager(instance);
        lootManager = new LootManager(instance);
        mobManager  = new MobManager(instance);
        customRecipeManager = new CustomRecipeManager(instance);
        playerManager = new PlayerManager(instance, getManaManager());
        sellLoader = new SellLoader(instance);
        skillManager = new SkillManager(instance, getManaManager());
        taskManager = new TaskManager();
        guiManager = new GuiManager();
        overworldManager = new OverworldManager(instance);
        vaultAndSpawnerManager = new VaultAndSpawnerManager(instance);
        netheriteSmithingManager = new NetheriteSmithingManager(instance);
        partyManager = new PartyManager(instance);

        netheriteSmithingManager.initialize();
        sellLoader.initialize();

        //External plugins
        economyAPI = new DawnExcellentEconomy(instance);


        log.info(TextUtil.format("<green>Managers and Loaders Registered!</green>"));
    }

    private void registerTasks() {
        log.info(TextUtil.format("<yellow>Tasks...</yellow>"));
        ManaTasks.initManaTasks(instance);
        BankAutoSaveTask.runBankAutoSave(instance);
        TemporaryBlockTask.runTemporaryBlockTask(instance);
        CooldownCleanupTask.startCooldownCleanup();
        log.info(TextUtil.format("<green>Tasks Registered!</green>"));
    }

    private void hookOptionalPlugins() {
        if (Bukkit.getPluginManager().isPluginEnabled("FancyNpcs")) {
            getServer().getPluginManager().registerEvents(new FancyNPCEvents(instance), instance);
            log.info(TextUtil.format("<green>FancyNpcs hooked!</green>"));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ExcellentEconomy")) {
            economyAPI.init();
            //getServer().getPluginManager().registerEvents(new PlayerBreakingBlockListener(instance), instance);
            log.info(TextUtil.format("<green>ExcellentEconomy hooked!</green>"));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new DawnBankPlaceholder(instance).register();
            log.info(TextUtil.format("<green>PlaceholderAPI hooked!</green>"));
        }
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
    public CustomItemFactory getCustomItemFactory() {return customItemFactory;}
    public LootManager getLootManager() {return lootManager;} //loot manager needs custom item factory
    public MobManager getMobManager() {return mobManager;} //mob manager needs loot manager
    public ManaManager getManaManager() {return manaManager;}
    public PlayerManager getPlayerManager() {return playerManager;}
    public SkillManager getSkillManager() {return skillManager;}
    public TaskManager getTaskManager() {return taskManager;}
    public CustomRecipeManager getCustomRecipeManager() {return customRecipeManager;}
    public GuiManager getGuiManager() {return guiManager;}
    public DawnExcellentEconomy getEconomyAPI() {return economyAPI;}
    public DatabaseManager getDatabaseManager() {return databaseManager;}
    public BankDAO getBankDAO() {return bankDAO;}
    public ProtectedChunkDAO getChunkDAO() {return chunkDAO;}
    public SellLoader getSellLoader() {return sellLoader;}
    public CustomItemManager getCustomItemManager() {return customItemManager;}
    public OverworldManager getOverworldManager() {return overworldManager;}
    public TrackedChestDAO getChestDAO() {return chestDAO;}
    public PlayerDAO getPlayerDAO() {return playerDAO;}
    public VaultAndSpawnerManager getVaultAndSpawnerManager() {return vaultAndSpawnerManager;}
    public PartyManager getPartyManager() {return partyManager;}

    @NotNull
    public ComponentLogger getLog() {return log;}
}
