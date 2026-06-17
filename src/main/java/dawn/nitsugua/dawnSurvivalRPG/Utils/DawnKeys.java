package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.NamespacedKey;

public class DawnKeys {
    public static NamespacedKey ITEM_ID;
    public static NamespacedKey ITEM_VERSION;
    public static NamespacedKey ITEM_UUID;
    public static NamespacedKey COOLDOWN;
    public static NamespacedKey MANA;
    public static NamespacedKey MAX_MANA;
    public static NamespacedKey SKILL_LEVEL;
    public static NamespacedKey SKILL_EXP;
    public static NamespacedKey OWNER_UUID;
    public static NamespacedKey USES;

    public static void init(DawnSurvivalRPG plugin) {
        ITEM_ID = new NamespacedKey(plugin, "item_id");
        ITEM_VERSION = new NamespacedKey(plugin, "item_version");
        ITEM_UUID = new NamespacedKey(plugin, "item_uuid");
        COOLDOWN = new NamespacedKey(plugin, "cooldown");
        MANA = new NamespacedKey(plugin, "mana");
        MAX_MANA = new NamespacedKey(plugin, "max_mana");
        SKILL_LEVEL = new NamespacedKey(plugin, "skill_level");
        SKILL_EXP = new NamespacedKey(plugin, "skill_exp");
        OWNER_UUID = new NamespacedKey(plugin, "owner_uuid");
        USES = new NamespacedKey(plugin, "uses");
    }
}
