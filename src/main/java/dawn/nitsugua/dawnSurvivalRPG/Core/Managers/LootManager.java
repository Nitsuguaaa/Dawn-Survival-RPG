package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemFactory;
import dawn.nitsugua.dawnSurvivalRPG.Loots.LootTable;
import dawn.nitsugua.dawnSurvivalRPG.Loots.LootTableEntry;
import dawn.nitsugua.dawnSurvivalRPG.Loots.LootTableLoader;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final CustomItemFactory customItemFactory;
    private final DawnSurvivalRPG plugin;

    public LootManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.customItemFactory = plugin.getCustomItemFactory();
    }

    public List<ItemStack> rollLoot(LootTable table) {
        List<ItemStack> drops = new ArrayList<>();
        if(table == null) return drops;

        for(LootTableEntry entry : table.getEntries()) {
            if(ThreadLocalRandom.current().nextDouble() > entry.getLootChance()) continue;

            int amount = entry.getLootMin() + ThreadLocalRandom.current().nextInt(entry.getLootMax() - entry.getLootMin() + 1);

            ItemStack stack = resolveItem(entry.getItemKey(), amount);
            if(stack != null) drops.add(stack);
        }
        return drops;
    }

    public ItemStack resolveItem(String key, int amount) {
        ItemStack custom = customItemFactory.createCustomItem(key, amount);
        if(custom != null) return custom;

        Material material = Material.matchMaterial(key);
        if(material != null) return new ItemStack(material, amount);

        plugin.getLog().info(TextUtil.sendLogInfo("LootManager", TextUtil.LogInfo.ERROR,"Unknown loot item: <white>" + key));
        return null;
    }

    public void reload() {
        dawnRegistry.clearLootTables();
        LootTableLoader.initialize(plugin);
    }
}
