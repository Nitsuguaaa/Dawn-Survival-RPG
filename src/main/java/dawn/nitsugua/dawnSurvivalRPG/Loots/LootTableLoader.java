package dawn.nitsugua.dawnSurvivalRPG.Loots;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootTableLoader {
    public static void initialize(DawnSurvivalRPG plugin) {
        DawnRegistry dawnRegistry = DawnRegistry.getInstance();

        File folder = new File(plugin.getDataFolder(), "loots");
        if(!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles(((dir, name) -> name.endsWith(".yml")));
        if(files == null) return;

        for(File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for(String tableId : config.getKeys(false)) {
                try {
                    ConfigurationSection section = config.getConfigurationSection(tableId);
                    if(section == null) continue;

                    int xpMin = section.getInt("xp-min", 0);
                    int xpMax = section.getInt("xp-max", 0);

                    List<LootTableEntry> entries = new ArrayList<>();
                    for(Map<?,?> map : section.getMapList("entries")) {
                        String itemKey = (String) map.get("item");
                        if(itemKey == null) continue;

                        int min = map.containsKey("min") ? ((Number) map.get("min")).intValue() : 1;
                        int max = map.containsKey("max") ? ((Number) map.get("max")).intValue() : 1;
                        double chance = map.containsKey("chance") ? ((Number) map.get("chance")).doubleValue() : 1.0;

                        entries.add(new LootTableEntry(itemKey, min, max, chance));
                    }

                    dawnRegistry.registerLootTable(new LootTable(tableId, entries).setXpRates(xpMin, xpMax));
                    plugin.getLog().info(TextUtil.sendLogInfo("LootTableLoader", TextUtil.LogInfo.SUCCESS, "loot table registered: <white>" + tableId));
                } catch (Exception e) {
                    plugin.getLog().info(TextUtil.sendLogInfo("LootTableLoader", TextUtil.LogInfo.ERROR, "Failed to load loot table: <white>" + tableId));
                }
            }

        }
    }
}
