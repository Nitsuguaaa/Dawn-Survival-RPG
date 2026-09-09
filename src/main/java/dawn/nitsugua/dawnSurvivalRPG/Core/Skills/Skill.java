package dawn.nitsugua.dawnSurvivalRPG.Core.Skills;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Skill {
    boolean onUse(Player player, ItemStack item, DawnSurvivalRPG plugin);
    String getId();

    default int getDefaultManaCost() { return 0; }
    default int getDefaultCooldown() { return 0; }
}
