package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItemFactory {
    //this is where the items gets put into the world

    private DawnSurvivalRPG plugin;

    public CustomItemFactory(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public ItemStack createCustomItem(String item) {
        plugin.getLog().info("Got data: {}", item);
        CustomItem customItem = plugin.getCustomItemManager().getCustomItem(item);

        Material itemMaterial = customItem.getParentMaterial();
        ItemStack finalItem = new ItemStack(itemMaterial);

        ItemMeta itemMeta = finalItem.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        //SWITCH TO DYNAMIC RARITY USING YAML
        itemMeta.displayName(TextUtil.setItemRarity(TextUtil.ItemRarity.RARE, customItem.getItemName()));

        finalItem.setItemMeta(itemMeta);

        return finalItem;
    }
}
