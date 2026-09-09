package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.Skill;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SkillManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final DawnSurvivalRPG plugin;
    private final ManaManager manaManager;

    public SkillManager(DawnSurvivalRPG plugin, ManaManager manaManager) {
        this.plugin = plugin;
        this.manaManager = manaManager;
    }

    public boolean onUse(Player player, ItemStack item, PersistentDataContainer pdc) {
        String itemId = pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
        Skill skill = dawnRegistry.getSkill(pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING) + "_SKILL");
        if(skill == null) return false;

        boolean success = skill.onUse(player, item);
        if(success) {
            manaManager.onManaItemUse(dawnRegistry.getCustomItem(itemId), player);
            plugin.getCooldownManager().setCooldown(player, item);
        }
        return success;
    }
}
