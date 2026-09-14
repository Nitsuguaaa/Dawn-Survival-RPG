package dawn.nitsugua.dawnSurvivalRPG.Core.Skills;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DungeonPearlSkill implements Skill {

    @Override
    public boolean onUse(Player player, ItemStack item, DawnSurvivalRPG plugin) {
        DawnRegistry registry = DawnRegistry.getInstance();

        // --- Step 1: Setup ---
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();
        Location current = start.clone();

        double MAX_DISTANCE = 10.0;
        int maxIterations = (int) (MAX_DISTANCE * 10); // 0.1 step per iteration

        boolean foundWall = false;
        double distanceToWall = -1;

        // --- Step 2: Raycast loop ---
        for (int i = 0; i < maxIterations; i++) {
            current.add(direction.clone().multiply(0.1));
            Block block = current.getBlock();
            double distance = start.distance(current);

            // --- Step 3: Find the first dungeon wall ---
            if (!foundWall && isDungeonTerrain(block.getType())) {
                foundWall = true;
                distanceToWall = distance;
                continue;
            }

            // --- Step 4: After wall, look for first air block ---
            if (foundWall && isPassable(block.getType())) {
                // --- Step 5: Distance check to wall (must be close) ---
                if (distanceToWall > 2.0) {
                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                            "You must be closer to the structure!"));
                    return false;
                }

                // --- Step 6: Found valid air block behind wall ---
                Location teleportLoc = block.getLocation().clone();

                teleportLoc.setX(block.getX() + 0.5);
                teleportLoc.setY(block.getY());
                teleportLoc.setZ(block.getZ() + 0.5);
                teleportLoc.setYaw(player.getYaw());
                teleportLoc.setPitch(player.getPitch());

                // --- Step 7: Check if there's headroom (above block) ---
                Block headroom = block.getRelative(BlockFace.UP);
                if (headroom.getType().isSolid()) {
                    // 🧱 Solid block above → player is in a tight space → crawling
                    player.setPose(Pose.SWIMMING);
                } else {
                    player.setPose(Pose.STANDING);
                }

                // --- Step 8: Final check: Is destination in a protected structure? ---
                if (!registry.isProtected(block.getWorld().getName(), block.getChunk().getX(), block.getChunk().getZ())) {
                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                            "The Dungeon Pearl only works on protected structures!"));
                    return false;
                }

                // --- Step 9: Teleport the player ---
                player.teleport(teleportLoc);
                player.getWorld().spawnParticle(Particle.PORTAL, teleportLoc, 30, 0.5, 0.5, 0.5, 0.1);
                player.playSound(teleportLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
                player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Passed through the structure!"));

                return true; // ✅ Teleport successful
            }
        }

        // --- Step 10: No valid teleport found ---
        if (!foundWall) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                    "No structure wall found in front of you!"));
        } else {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                    "The Dungeon Pearl failed to phase!"));
        }

        return false;
    }
    @Override
    public String getId() {
        return "DUNGEON_PEARL_SKILL";
    }

    // Helper: Define what blocks the pearl can phase through (terrain)
    private boolean isPassable(Material m) {
        return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR ||
                m == Material.TALL_GRASS || m == Material.FERN || m == Material.WATER;
    }

    // NEW HELPER: What counts as a "Dungeon Wall" that CAN be phased through?
    private boolean isDungeonTerrain(Material m) {
        return m == Material.STONE || m == Material.DEEPSLATE || m == Material.COBBLED_DEEPSLATE ||
                m == Material.DEEPSLATE_BRICKS || m == Material.DEEPSLATE_TILES ||
                m == Material.TUFF || m == Material.CALCITE || m == Material.DRIPSTONE_BLOCK ||
                m == Material.ANDESITE || m == Material.DIORITE || m == Material.GRANITE ||
                m == Material.DIRT || m == Material.GRASS_BLOCK || m == Material.GRAVEL ||
                m == Material.SAND || m == Material.SANDSTONE || m == Material.OBSIDIAN ||
                m == Material.CRYING_OBSIDIAN || m == Material.REINFORCED_DEEPSLATE || m == Material.IRON_BARS ||
                m == Material.WAXED_COPPER_GRATE || m == Material.CHISELED_STONE_BRICKS; // Trial chamber blocks!
    }
}
