package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;

public class DawnKeys {
    public static NamespacedKey DAWN_OBJECT;
    public static NamespacedKey ITEM_ID;
    public static NamespacedKey ITEM_VERSION;
    public static NamespacedKey ITEM_UUID;
    public static NamespacedKey COOLDOWN;
    //DEPRECATED: public static NamespacedKey LAST_USED; uses in-cache memory cooldowns now
    public static NamespacedKey MANA;
    public static NamespacedKey MAX_MANA;
    public static NamespacedKey SKILL_TYPE;
    public static NamespacedKey SKILL_LEVEL;
    public static NamespacedKey SKILL_EXP;
    public static NamespacedKey OWNER_UUID;
    public static NamespacedKey USES;
    public static NamespacedKey CATEGORY;
    public static NamespacedKey SUB_CATEGORY;
    public static NamespacedKey ONE_TIME_USE;


    //for entities
    public static NamespacedKey DAWN_MOB; //bool
    public static NamespacedKey DAWN_MOB_TYPE; //string
    public static NamespacedKey DAWN_MOB_NAME;
    public static NamespacedKey SPAWNER_MOB; //bool, for spawner and trial spawner spawned mobs

    public static NamespacedKey SPAWNER_UUID;
    public static NamespacedKey NODE_ID_STRING;

    //Attributes
    public static NamespacedKey INTELLIGENCE;

    //for Elytra
    public static NamespacedKey FIXED;

    public static void init(DawnSurvivalRPG plugin) {
        DAWN_OBJECT = new NamespacedKey(plugin, "dawn_object");
        ITEM_ID = new NamespacedKey(plugin, "item_id");
        ITEM_VERSION = new NamespacedKey(plugin, "item_version");
        ITEM_UUID = new NamespacedKey(plugin, "item_uuid");
        COOLDOWN = new NamespacedKey(plugin, "cooldown");
        //DEPRECATED: LAST_USED = new NamespacedKey(plugin, "last-used");
        MANA = new NamespacedKey(plugin, "mana");
        MAX_MANA = new NamespacedKey(plugin, "max_mana");
        SKILL_TYPE = new NamespacedKey(plugin, "skill_type");
        SKILL_LEVEL = new NamespacedKey(plugin, "skill_level");
        SKILL_EXP = new NamespacedKey(plugin, "skill_exp");
        OWNER_UUID = new NamespacedKey(plugin, "owner_uuid");
        USES = new NamespacedKey(plugin, "uses"); //if the item has a right-click usage
        CATEGORY = new NamespacedKey(plugin, "category");
        SUB_CATEGORY = new NamespacedKey(plugin, "sub-category");
        ONE_TIME_USE = new NamespacedKey(plugin, "one-time-use");

        INTELLIGENCE = new NamespacedKey(plugin, "intelligence");

        DAWN_MOB = new NamespacedKey(plugin, "dawn_mob");
        DAWN_MOB_TYPE = new NamespacedKey(plugin, "dawn_mob_type");
        DAWN_MOB_NAME = new NamespacedKey(plugin, "dawn_mob_name");

        SPAWNER_UUID = new NamespacedKey(plugin, "spawner_uuid");
        NODE_ID_STRING = new NamespacedKey(plugin, "node_id_string");
        FIXED = new NamespacedKey(plugin, "is_fixed");
        SPAWNER_MOB = new NamespacedKey(plugin, "spawner-mob");
    }

    public static boolean isDawnObject(ItemStack item) {
        if(item == null) return false;
        if(item.getType() == Material.AIR) return false;
        if(!item.hasItemMeta()) return false;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(DawnKeys.DAWN_OBJECT, PersistentDataType.BOOLEAN);
    }
}
