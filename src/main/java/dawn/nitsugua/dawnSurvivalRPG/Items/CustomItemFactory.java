package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.Weapons.CustomWeapon;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomItemFactory {
    //this is where the items gets put into the world

    //set defaults just in case?
    private final List<Component> defaultLore = new ArrayList<>();
    private int defaultWeaponDamage;

    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    private ItemStack finalItem;

    public CustomItemFactory(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.defaultLore.add(TextUtil.setItemLore("<gray>Custom Item"));
    }

    public ItemStack createCustomItem(String item) {
        //debug: plugin.getLog().info("Got data: {}", item);
        CustomItem customItem = dawnRegistry.getCustomItem(item);

        //assert customItem != null;
        Material itemMaterial = customItem.getParentMaterial();
        finalItem = new ItemStack(itemMaterial); //the itemStack to be returned

        ItemMeta itemMeta = finalItem.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        //itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.displayName(TextUtil.setItemRarity(customItem.getRarity(), customItem.getItemName()));

        if(customItem.getLore() != null) {
            itemMeta.lore(customItem.getLore());
        } else {
            itemMeta.lore(defaultLore);
        }

        finalItem.setItemMeta(itemMeta); //sets default custom item

        switch (customItem.getCategory()) { //modifies default custom item to specific category
            case "WEAPON":
                customWeapon(customItem);
                break;
            case "ARMOR":
                plugin.getLog().info(TextUtil.sendLogInfo("CustomItemFactory", TextUtil.LogInfo.WARNING, "Custom Armor not set."));
                break;
            default:
                return null;
        }



        return finalItem;
    }

    private void customWeapon(CustomItem customItem) {
        if(!(customItem instanceof CustomWeapon customWeapon)) {
            return;
        }

        ItemMeta itemMeta = finalItem.getItemMeta();

        AttributeModifier newAttribute = new AttributeModifier(
                NamespacedKey.minecraft("base_attack_damage"),
                customWeapon.getWeaponDamage() - 1, //-1 because of Minecraft weird constant 1
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );

        switch (customItem.getSubCategory()) {
            case "SWORD":
                itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, newAttribute);

                finalItem.setItemMeta(itemMeta);
                return;
            case "BOW":
                plugin.getLog().info(TextUtil.sendLogInfo("CustomItemFactory", TextUtil.LogInfo.WARNING, "Custom Bow not set."));
                break;
        }
    }
}
