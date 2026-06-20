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

    public SkillManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.manaManager = new ManaManager(plugin);
    }

    public void onUse(Player player, ItemStack item, PersistentDataContainer pdc) {
        Skill skill = dawnRegistry.getSkill(pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING) + "_SKILL");
        if(skill != null) {
            if(skill.onUse(player, item)) {
                manaManager.onManaItemUse(dawnRegistry.getCustomItem(pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING)));
            }
        }
    }
}
