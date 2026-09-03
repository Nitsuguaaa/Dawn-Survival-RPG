package dawn.nitsugua.dawnSurvivalRPG.Economy;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class SellLoader {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dr = DawnRegistry.getInstance();

    public SellLoader(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        plugin.getLog().info(TextUtil.sendLogInfo("SellLoader", TextUtil.LogInfo.INFO, "Registering sell prices..."));
        File sellList = new File(plugin.getDataFolder(), "sell-list.yml");
        if(!sellList.exists()) {
            try {
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.save(sellList);
            } catch (IOException _) {}

        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(sellList);
        ConfigurationSection pricesSection = yaml.getConfigurationSection("prices");

        if (pricesSection == null) {
            plugin.getLog().info(TextUtil.sendLogInfo("SellLoader", TextUtil.LogInfo.ERROR, "No 'prices' section found in sell-list.yml!"));
            return;
        }

        dr.getAllPrices().clear();
        for(String key : pricesSection.getKeys(false)) {
            double price = pricesSection.getDouble(key);
            dr.registerPrice(key.toUpperCase(), price);
        }


        plugin.getLog().info(TextUtil.sendLogInfo("SellLoader", TextUtil.LogInfo.SUCCESS, "Sell prices registered!"));
    }

    public boolean isSellable(String item) {
        return dr.getAllPrices().containsKey(item);
    }

    public void sellAllItems(Player player) {
        double totalEarned = 0.0;

        // 1. Iterate by index so we can safely clear slots
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);

            // 2. Skip empty slots and AIR blocks safely
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            String identifier = "";

            if (DawnKeys.isDawnObject(item)) {
                identifier = item.getItemMeta().getPersistentDataContainer().get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
            } else {
                identifier = item.getType().name();
            }

            // 3. Safety check for identifier just in case
            if (identifier != null && isSellable(identifier)) {
                double price = dr.getSellPrice(identifier);
                double itemTotal = price * item.getAmount();

                totalEarned += itemTotal;

                // 4. Clear the specific slot safely instead of using .remove()
                player.getInventory().setItem(i, null);
            }
        }

        double tax = totalEarned * 0.1;
        double finalPayout = totalEarned - tax;

        // 5. Use a small threshold for floating point comparison
        if (finalPayout < 0.01) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You have nothing to sell"));
            return;
        }

        plugin.getEconomyAPI().getEconomyAPI().deposit(player, "money", finalPayout);

        // 6. Format to 2 decimal places so it looks clean (e.g., 14.40 instead of 14.4)
        String formattedPayout = String.format("%.2f", finalPayout);
        String formattedTax = String.format("%.2f", tax);

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                "Sold items for <gold>₱" + formattedPayout + " <gray>(10% tax: ₱" + formattedTax + ")"));
    }

    public void reload() {
        dr.getAllPrices().clear();
        initialize();
    }
}
