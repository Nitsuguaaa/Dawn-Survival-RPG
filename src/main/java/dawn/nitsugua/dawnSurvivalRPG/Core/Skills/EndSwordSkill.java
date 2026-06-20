package dawn.nitsugua.dawnSurvivalRPG.Core.Skills;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EndSwordSkill implements Skill {
    private static final int MAX_RANGE = 9;
    private static final double STEP_SIZE = 0.5;
    private static final double PADDING = 0.4;

    @Override
    public String getId() {
        return "END_SWORD_SKILL";
    }

    @Override
    public boolean onUse(Player player, ItemStack item) {
        Location destination = findTeleportLocation(player);

        if (destination == null) {
            player.sendMessage(MessagesUtil.sendInfo(
                    MessagesUtil.commonMessages.FAILURE,
                    "There are blocks in the way"
            ));
            return false;
        }

        Bukkit.getScheduler().runTask(DawnSurvivalRPG.getInstance(), () -> {
            player.teleport(destination);
            player.sendMessage(MessagesUtil.sendInfo(
                    MessagesUtil.commonMessages.SUCCESS,
                    "Teleported!"
            ));
        });
        return true;
    }

    public Location findTeleportLocation(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        // ============================================================
        // CHECK 1: Head or front block is solid
        // ============================================================
        Block headBlock = eyeLocation.getBlock();
        Block frontBlock = eyeLocation.clone().add(direction).getBlock();

        if (headBlock.getType().isSolid()) {
            player.sendMessage(MessagesUtil.sendInfo(
                    MessagesUtil.commonMessages.FAILURE,
                    "Your head is inside a block!"
            ));
            return null;
        }
        if (frontBlock.getType().isSolid()) {
            player.sendMessage(MessagesUtil.sendInfo(
                    MessagesUtil.commonMessages.FAILURE,
                    "There's a block right in front of your face!"
            ));
            return null;
        }

        double lastSafeDistance = 0;

        // ============================================================
        // MAIN LOOP: Walk forward step by step
        // ============================================================
        for (double distance = 0.1; distance <= MAX_RANGE; distance += STEP_SIZE) {
            Vector step = direction.clone().multiply(distance);
            Location checkLocation = eyeLocation.clone().add(step);
            Block block = checkLocation.getBlock();

            if (block.getType().isSolid()) {
                double blockY = block.getY();
                double playerY = player.getY();

                // ============================================================
                // CHECK 2: GROUND TELEPORT (block below feet)
                // ============================================================
                if (blockY < playerY - 0.5) {
                    Location surfaceLocation = findSurfaceLocation(block.getLocation(), player);
                    if (surfaceLocation != null) {
                        surfaceLocation.setYaw(player.getYaw());
                        surfaceLocation.setPitch(player.getPitch());

                        // Check if surface location is safe
                        if (isSafeLocation(surfaceLocation)) {
                            return surfaceLocation; // ✅ GROUND TELEPORT SUCCESS
                        } else {
                            player.sendMessage(MessagesUtil.sendInfo(
                                    MessagesUtil.commonMessages.FAILURE,
                                    "Surface location is not safe! (Check feet/head)"
                            ));
                            return null;
                        }
                    } else {
                        player.sendMessage(MessagesUtil.sendInfo(
                                MessagesUtil.commonMessages.FAILURE,
                                "Could not find a surface above the ground!"
                        ));
                        return null;
                    }
                }

                // ============================================================
                // CHECK 3: WALL TELEPORT (block at or above feet)
                // ============================================================
                double teleportDistance = lastSafeDistance - PADDING;

                if (teleportDistance < 0.1) {
                    player.sendMessage(MessagesUtil.sendInfo(
                            MessagesUtil.commonMessages.FAILURE,
                            "Too close to the wall! (Distance: " + String.format("%.2f", teleportDistance) + ")"
                    ));
                    return null;
                }

                Vector safeStep = direction.clone().multiply(teleportDistance);
                Location safeLocation = eyeLocation.clone().add(safeStep);

                safeLocation.setYaw(player.getYaw());
                safeLocation.setPitch(player.getPitch());

                // Check if wall teleport location is safe
                if (isSafeLocation(safeLocation)) {
                    return safeLocation; // ✅ WALL TELEPORT SUCCESS
                } else {
                    player.sendMessage(MessagesUtil.sendInfo(
                            MessagesUtil.commonMessages.FAILURE,
                            "Wall teleport location is not safe! (Feet: " + safeLocation.getBlock().getType().name() +
                                    ", Head: " + safeLocation.clone().add(0, 1, 0).getBlock().getType().name() + ")"
                    ));
                    return null;
                }
            }

            lastSafeDistance = distance;
        }

        // ============================================================
        // CHECK 4: NO OBSTACLES - Teleport to max range
        // ============================================================
        Vector finalStep = direction.clone().multiply(MAX_RANGE);
        Location finalLocation = eyeLocation.clone().add(finalStep);

        finalLocation.setYaw(player.getYaw());
        finalLocation.setPitch(player.getPitch());

        if (isSafeLocation(finalLocation)) {
            return finalLocation; // ✅ OPEN AREA TELEPORT SUCCESS
        } else {
            player.sendMessage(MessagesUtil.sendInfo(
                    MessagesUtil.commonMessages.FAILURE,
                    "Destination is not safe! (Feet: " + finalLocation.getBlock().getType().name() +
                            ", Head: " + finalLocation.clone().add(0, 1, 0).getBlock().getType().name() + ")"
            ));
        }

        return null;
    }

    // ============================================================
    // SAFETY CHECK
    // ============================================================
    public boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        Block head = location.clone().add(0, 1, 0).getBlock();

        if (feet.getType().isSolid()) return false;
        if (head.getType().isSolid()) return false;

        return true;
    }

    // ============================================================
    // SURFACE FINDER
    // ============================================================
    private Location findSurfaceLocation(Location blockLocation, Player player) {
        World world = blockLocation.getWorld();
        int x = blockLocation.getBlockX();
        int z = blockLocation.getBlockZ();
        int startY = blockLocation.getBlockY();

        for (int y = startY + 1; y <= world.getMaxHeight(); y++) {
            Location checkLoc = new Location(world, x + 0.5, y, z + 0.5);
            Block checkBlock = checkLoc.getBlock();

            if (checkBlock.getType() == Material.AIR) {
                Location aboveLoc = checkLoc.clone().add(0, 1, 0);
                if (aboveLoc.getBlock().getType() == Material.AIR) {
                    return checkLoc;
                }
            }
        }
        return null;
    }
}