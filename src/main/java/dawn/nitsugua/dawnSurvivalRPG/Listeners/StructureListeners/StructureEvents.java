package dawn.nitsugua.dawnSurvivalRPG.Listeners.StructureListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.world.AsyncStructureGenerateEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.BoundingBox;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class StructureEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public StructureEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onStructureGenerate(AsyncStructureGenerateEvent e) {
        Registry<Structure> structureRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE);
        NamespacedKey key = structureRegistry.getKey(e.getStructure());
        if(key == null) return;

        String structureKey = key.toString();
        int[] yRange = dawnRegistry.getProtectionYRange(structureKey);
        if(yRange == null) return;

        BoundingBox box = e.getBoundingBox();
        int chunkX = (int) box.getMinX() >> 4;
        int chunkZ = (int) box.getMinZ() >> 4;
        String worldName = e.getWorld().getName();

        Bukkit.getScheduler().runTask(plugin, () -> {
            dawnRegistry.protectChunk(worldName, chunkX, chunkZ, yRange[0], yRange[1], structureKey);
            plugin.getChunkDAO().saveChunkAsync(worldName, chunkX, chunkZ, yRange[0], yRange[1], structureKey);

            plugin.getLogger().info("[DAWN] Protected " + structureKey +
                    " at chunk (" + chunkX + ", " + chunkZ + ")" +
                    " | Y: " + yRange[0] + " to " + yRange[1]);
        });
    }

    private static final Set<Material> ALLOWED_TEMPORARY_BLOCKS = Set.of(
            Material.TORCH,
            Material.WALL_TORCH,
            Material.COPPER_TORCH,
            Material.COPPER_WALL_TORCH,
            Material.SOUL_TORCH,
            Material.SOUL_WALL_TORCH
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Chunk chunk = e.getBlock().getChunk();
        String worldName = e.getBlock().getWorld().getName();
        int chunkZ = chunk.getZ();
        int chunkX = chunk.getX();
        int blockY = e.getBlock().getY();

        if(dawnRegistry.isProtected(worldName, chunkX, chunkZ)) {
            int[] yRange = dawnRegistry.getYRange(worldName, chunkX, chunkZ);

            if(blockY >= yRange[0] && blockY <= yRange[1]) {
                if(ALLOWED_TEMPORARY_BLOCKS.contains(e.getBlock().getType()) && dawnRegistry.getAllTemporaryBlocks().containsKey(e.getBlock().getLocation())) {
                    dawnRegistry.removeTemporaryBlock(e.getBlock().getLocation());
                    return;
                }
                e.setCancelled(true);
                e.getPlayer().sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "This area seems to be protected."));
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        Chunk chunk = e.getBlock().getChunk();
        String worldName = e.getBlock().getWorld().getName();
        int chunkZ = chunk.getZ();
        int chunkX = chunk.getX();
        int blockY = e.getBlock().getY();

        if(dawnRegistry.isProtected(worldName, chunkX, chunkZ)) {
            int[] yRange = dawnRegistry.getYRange(worldName, chunkX, chunkZ);

            if(blockY >= yRange[0] && blockY <= yRange[1]) {
                if(ALLOWED_TEMPORARY_BLOCKS.contains(e.getBlock().getType())) {
                    plugin.getOverworldManager().trackBlock(e.getBlock(), e.getBlock().getType(), TimeUnit.SECONDS.toMillis(3));
                    return;
                }
                e.setCancelled(true);
                e.getPlayer().sendMessage("The ground here is protected.");
            }
        }
    }

    @EventHandler
    public void onWaterPlace(PlayerBucketEmptyEvent e) {
        if(e.getBucket() != Material.WATER_BUCKET) {
            e.setCancelled(true);
            return;
        }

        Block clickedBlock = e.getBlockClicked();
        Block targetBlock;

        if(clickedBlock.getBlockData() instanceof Waterlogged) {
            targetBlock = clickedBlock;
        } else {
            targetBlock = clickedBlock.getRelative(e.getBlockFace());
        }

        Location loc = targetBlock.getLocation();
        String worldName = loc.getWorld().getName();
        int chunkX = loc.getChunk().getX();
        int chunkZ = loc.getChunk().getZ();
        int blockY = loc.getBlockY();

        if (dawnRegistry.isProtected(worldName, chunkX, chunkZ)) {
            int[] yRange = dawnRegistry.getYRange(worldName, chunkX, chunkZ);

            if (blockY >= yRange[0] && blockY <= yRange[1]) {
                // 4. Track it!
                // We pass Material.WATER explicitly because the block is currently AIR
                plugin.getOverworldManager().trackBlock(targetBlock, Material.WATER, TimeUnit.SECONDS.toMillis(5));
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        Block targetBlock = e.getBlockClicked().getRelative(e.getBlockFace());
        Location loc = targetBlock.getLocation();

        // If they scoop up a tracked temporary block, stop tracking it
        if (dawnRegistry.getAllTemporaryBlocks().containsKey(loc)) {
            dawnRegistry.removeTemporaryBlock(loc);
            dawnRegistry.removeExpectedType(loc);
        }
    }

    @EventHandler
    public void onFireStart(BlockIgniteEvent e) {
        Chunk chunk = e.getBlock().getChunk();
        String worldName = e.getBlock().getWorld().getName();
        int chunkZ = chunk.getZ();
        int chunkX = chunk.getX();
        int blockY = e.getBlock().getY();

        if(dawnRegistry.isProtected(worldName, chunkX, chunkZ)) {
            int[] yRange = dawnRegistry.getYRange(worldName, chunkX, chunkZ);

            if(blockY >= yRange[0] && blockY <= yRange[1]) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("The ground here is protected.");
            }
        }
    }



}
