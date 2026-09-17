package dawn.nitsugua.dawnSurvivalRPG.Core.Skills;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Vault;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class ChronosGearSkill implements Skill {
    @Override
    public boolean onUse(Player player, ItemStack item, DawnSurvivalRPG plugin) {
        // 1. Native RayTrace (Max 2.0 blocks distance)
        RayTraceResult result = player.rayTraceBlocks(2.0);

        // 2. Check if we hit anything
        if (result != null && result.getHitBlock() != null) {
            Block block = result.getHitBlock();

            // 3. Check if it's a Vault
            if (block.getState() instanceof Vault vault) {
                if (vault.removeRewardedPlayer(player.getUniqueId())) {
                    vault.update(); // Save the state back to the world

                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Vault rewound!"));

                    // Add some Chronos "Time Rewind" juice!
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0f, 0.5f);
                    player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, block.getLocation().add(0.5, 0.5, 0.5), 30, 0.3, 0.3, 0.3, 0.1);

                    return true;
                } else {
                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You haven't opened this vault yet!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return false;
                }
            }
        }

        // Optional: Play a "tick" sound if they swing at the air so it feels responsive
        player.playSound(player.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_BREAK, 0.5f, 2.0f);
        return false;
    }

    @Override
    public String getId() {
        return "CHRONOS_GEAR_SKILL";
    }
}
