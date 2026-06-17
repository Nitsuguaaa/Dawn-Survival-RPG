package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DawnKeys {
    public static NamespacedKey DAWN_OBJECT;
    public static NamespacedKey ITEM_ID;
    public static NamespacedKey ITEM_VERSION;
    public static NamespacedKey ITEM_UUID;
    public static NamespacedKey COOLDOWN;
    public static NamespacedKey MANA;
    public static NamespacedKey MAX_MANA;
    public static NamespacedKey SKILL_TYPE;
    public static NamespacedKey SKILL_LEVEL;
    public static NamespacedKey SKILL_EXP;
    public static NamespacedKey OWNER_UUID;
    public static NamespacedKey USES;
    public static NamespacedKey CATEGORY;
    public static NamespacedKey SUB_CATEGORY;

    public static void init(DawnSurvivalRPG plugin) {
        DAWN_OBJECT = new NamespacedKey(plugin, "dawn_object");
        ITEM_ID = new NamespacedKey(plugin, "item_id");
        ITEM_VERSION = new NamespacedKey(plugin, "item_version");
        ITEM_UUID = new NamespacedKey(plugin, "item_uuid");
        COOLDOWN = new NamespacedKey(plugin, "cooldown");
        MANA = new NamespacedKey(plugin, "mana");
        MAX_MANA = new NamespacedKey(plugin, "max_mana");
        SKILL_TYPE = new NamespacedKey(plugin, "skill_type");
        SKILL_LEVEL = new NamespacedKey(plugin, "skill_level");
        SKILL_EXP = new NamespacedKey(plugin, "skill_exp");
        OWNER_UUID = new NamespacedKey(plugin, "owner_uuid");
        USES = new NamespacedKey(plugin, "uses"); //if the item has a right-click usage
        CATEGORY = new NamespacedKey(plugin, "category");
        SUB_CATEGORY = new NamespacedKey(plugin, "sub-category");
    }

    public static boolean isDawnObject(PersistentDataContainer pdc) {
        return pdc.has(DawnKeys.DAWN_OBJECT, PersistentDataType.BOOLEAN);
    }
}
