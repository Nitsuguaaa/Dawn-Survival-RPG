package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.Weapons.CustomWeapon;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CustomItemFactory {
    //this is where the items gets put into the world

    //set defaults just in case?
    private final List<Component> defaultLore = new ArrayList<>();
    private int defaultWeaponDamage;

    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    //private

    public CustomItemFactory(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.defaultLore.add(TextUtil.setItemLore("<gray>Custom Item"));
    }

    public ItemStack createCustomItem(String item, int amount) {
        ItemStack finalItem;

        //debug: plugin.getLog().info("Got data: {}", item);
        CustomItem customItem = dawnRegistry.getCustomItem(item);

        //assert customItem != null;
        Material itemMaterial = customItem.getParentMaterial();
        finalItem = new ItemStack(itemMaterial, amount); //the itemStack to be returned

        ItemMeta itemMeta = finalItem.getItemMeta();

        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        pdc.set(DawnKeys.DAWN_OBJECT, PersistentDataType.BOOLEAN, true);
        pdc.set(DawnKeys.ITEM_ID, PersistentDataType.STRING, customItem.getCustomMaterial());
        pdc.set(DawnKeys.CATEGORY, PersistentDataType.STRING, customItem.getCategory());
        pdc.set(DawnKeys.SUB_CATEGORY, PersistentDataType.STRING, customItem.getSubCategory());

        //set attributes
        if(customItem.getIntelligence() != null) {
            //DEBUG plugin.getLog().info("Found intelligence! Setting to item right now");
            pdc.set(DawnKeys.INTELLIGENCE, PersistentDataType.INTEGER, customItem.getIntelligence());
        }

        if(customItem.isInteractable()) { //if the weapons has a skill
            // Store mana cost (default 0)
            Double mana = customItem.getManaCost() != null ? customItem.getManaCost() : 0;
            pdc.set(DawnKeys.MANA, PersistentDataType.DOUBLE, mana);

            pdc.set(DawnKeys.COOLDOWN, PersistentDataType.DOUBLE, customItem.getCooldown());

            // Store the skill type
            pdc.set(DawnKeys.SKILL_TYPE, PersistentDataType.STRING, customItem.getInteractableType());
        }


        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); //hide vanilla attributes
        //itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.displayName(TextUtil.setItemRarity(customItem.getRarity(), customItem.getItemName())); //set display name

        //decorative
        if(customItem.isGlinting()) {
            itemMeta.setEnchantmentGlintOverride(true);
        }

        if(customItem.getLore() != null) { //if the weapon has custom lore
            itemMeta.lore(customItem.getLore());
        } else {
            itemMeta.lore(defaultLore);
        }

        finalItem.setItemMeta(itemMeta); //sets default custom item

        switch (customItem.getCategory()) { //modifies default custom item to specific category
            case "WEAPON":
                return customWeapon(customItem, finalItem);
            case "ARMOR":
                plugin.getLog().info(TextUtil.sendLogInfo("CustomItemFactory", TextUtil.LogInfo.WARNING, "Custom Armor not set."));
                break;
            default:
                return finalItem;
        }
        return finalItem;
    }

    private ItemStack customWeapon(CustomItem customItem, ItemStack finalItem) {
        if(!(customItem instanceof CustomWeapon customWeapon)) {
            return finalItem;
        }

        ItemMeta itemMeta = finalItem.getItemMeta();

        //Might be a dumb idea but in my head, every custom weapon has either an active skill or a passive skill
        //for now it's a boolean, but it might turn into a list down the road if I want the item to use multiple systems
        itemMeta.getPersistentDataContainer().set(DawnKeys.USES, PersistentDataType.BOOLEAN, true);

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
                return finalItem;
            case "BOW":
                plugin.getLog().info(TextUtil.sendLogInfo("CustomItemFactory", TextUtil.LogInfo.WARNING, "Custom Bow not set."));
                break;
        }
        return finalItem;
    }
}
