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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DungeonPearlSkill implements Skill {

    @Override
    public boolean onUse(Player player, ItemStack item, DawnSurvivalRPG plugin) {
        DawnRegistry registry = DawnRegistry.getInstance();
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();
        Location current = start.clone();

        boolean foundWall = false;
        boolean hitSolid = false;

        double MAX_START_DISTANCE = 2.0;
        double MAX_PHASE_DISTANCE = 10.0;
        int maxIterations = (int) ((MAX_START_DISTANCE + MAX_PHASE_DISTANCE) * 10);

        for(int i = 0; i < maxIterations; i++) {
            current.add(direction.clone().multiply(0.1));
            Block block = current.getBlock();
            double distance = start.distance(current);

            if(!hitSolid && block.getType() != Material.AIR && !isPassable(block.getType())) {
                hitSolid = true;

                if(distance > MAX_START_DISTANCE) {
                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You must be closer to the structure!"));
                    return false;
                }
            }

            //check if solid block is hit (wall, ceiling, floor)
            if(hitSolid && isDungeonTerrain(block.getType())) {
                foundWall = true;
            }

            //If we have passed through a wall, look for a "Safe Pocket"
            if(foundWall) {
                // Check if we've traveled too far THROUGH the structure
                if (start.distance(current) > (MAX_START_DISTANCE + MAX_PHASE_DISTANCE)) {
                    break; // Break loop to trigger the final failure message
                }

                Block checkBlock = current.getBlock();
                Block aboveBlock = checkBlock.getRelative(BlockFace.UP);

                // Safe Pocket = Current block is AIR, and the block above (headroom) is also AIR
                if (checkBlock.getType() == Material.AIR && aboveBlock.getType() == Material.AIR) {

                    //Check if the DESTINATION chunk is protected (not the player's starting chunk)
                    if (!registry.isProtected(checkBlock.getWorld().getName(), checkBlock.getChunk().getX(), checkBlock.getChunk().getZ())) {
                        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "The Dungeon Pearl only works on protected structures!"));
                        return false;
                    }

                    //Center the player perfectly in the block to prevent suffocation clipping
                    Location teleportLoc = checkBlock.getLocation();
                    teleportLoc.setX(teleportLoc.getBlockX() + 0.5); // Center X
                    teleportLoc.setY(teleportLoc.getBlockY());        // Floor level
                    teleportLoc.setZ(teleportLoc.getBlockZ() + 0.5); // Center Z

                    teleportLoc.setYaw(player.getYaw());
                    teleportLoc.setPitch(player.getPitch());

                    player.teleport(teleportLoc);

                    // Spawn particles at the destination, play sound at the destination
                    player.getWorld().spawnParticle(Particle.PORTAL, teleportLoc, 30, 0.5, 0.5, 0.5, 0.1);
                    player.playSound(teleportLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);

                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Passed through the structure!"));
                    return true;
                }
            }
        }

        // Reached max range without finding a safe pocket
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                "The Dungeon Pearl failed to phase!"));
        return false; // Skill failed, cooldown will NOT be applied!
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
                m == Material.CRYING_OBSIDIAN || m == Material.REINFORCED_DEEPSLATE; // Trial chamber blocks!
    }
}
