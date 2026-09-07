package dawn.nitsugua.dawnSurvivalRPG.Core.Tasks;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class TemporaryBlockTask {
    private static DawnRegistry dr = DawnRegistry.getInstance();

    public static void runTemporaryBlockTask(DawnSurvivalRPG plugin) {
        ScheduledTask task = new ScheduledTask("CHECK_TEMPORARY_BLOCKS_TASK", () -> {
            if(dr.getAllTemporaryBlocks().isEmpty()) return;
            Map<Location, Long> temporaryBlocks = dr.getAllTemporaryBlocks();

            for(Map.Entry<Location, Long> block : temporaryBlocks.entrySet()) {
                Location loc = block.getKey();
                long expiryTime = block.getValue();

                if(System.currentTimeMillis() >= expiryTime) {
                    plugin.getOverworldManager().destroyBlock(loc);
                }
            }
        })
                .setInterval(1)
                .setTimeUnit(TimeUnit.SECOND);

        plugin.getTaskManager().registerTask(task);
    }
}
