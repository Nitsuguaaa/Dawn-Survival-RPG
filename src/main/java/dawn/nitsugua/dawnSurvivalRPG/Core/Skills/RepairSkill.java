package dawn.nitsugua.dawnSurvivalRPG.Core.Skills;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairSkill implements Skill{

    @Override
    public boolean onUse(Player player, ItemStack item, DawnSurvivalRPG plugin) {
        player.sendMessage("You're repairing?");
        return true;
    }

    @Override
    public String getId() {
        return "REPAIR_KIT_TIER_1_SKILL";
    }
}
