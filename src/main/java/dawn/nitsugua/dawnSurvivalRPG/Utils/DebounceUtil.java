package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;

import java.util.UUID;

public class DebounceUtil {
    private static final DawnRegistry dr = DawnRegistry.getInstance();
    private static final long DEBOUNCE_MS = 300;

    /**
     * Returns true if the player used *any* skill in the last 200ms.
     * Returns false and updates the timestamp if it's safe to proceed.
     */
    public static boolean isGlobalDebounceActive(UUID playerId) {
        String key = "GLOBAL_DEBOUNCE";
        Long lastUsed = dr.getCooldown(playerId, key);
        long now = System.currentTimeMillis();

        if (lastUsed != null && now - lastUsed < DEBOUNCE_MS) {
            return true; // Still debounced
        }

        // Update the debounce timestamp
        dr.setCooldown(playerId, key, now);
        return false;
    }

    public static void clearGlobalDebounce(UUID playerId) {
        dr.setCooldown(playerId, "GLOBAL_DEBOUNCE", 0L);
    }
}
