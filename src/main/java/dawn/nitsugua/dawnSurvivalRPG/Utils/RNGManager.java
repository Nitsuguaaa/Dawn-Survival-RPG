package dawn.nitsugua.dawnSurvivalRPG.Utils;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RNGManager {
    public static boolean roll(double chance) {
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

    public static int rand(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
