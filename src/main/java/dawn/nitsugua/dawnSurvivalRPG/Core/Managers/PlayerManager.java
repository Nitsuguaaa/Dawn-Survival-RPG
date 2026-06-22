package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final ManaManager manaManager;
    private final DawnSurvivalRPG plugin;

    public PlayerManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.manaManager = new ManaManager(plugin);
    }

    public void playerJoinEvent(Player player) {
        UUID playerId = player.getUniqueId();
        DawnPlayer dawnPlayer = new DawnPlayer(playerId);

        dawnRegistry.registerPlayer(playerId, dawnPlayer);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            initializePlayer(player);
        }, 1L);
    }

    public void playerLeaveEvent(Player player) {
        UUID playerId = player.getUniqueId();

        dawnRegistry.removePlayer(playerId);
    }

    public void initializePlayer(Player player) {
        //get armor too but for now just main hand item

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        manaManager.calculateMaxMana(player, mainHand, null);

        DawnPlayer dawnPlayer = dawnRegistry.getDawnPlayer(player.getUniqueId());
        if (dawnPlayer == null) return;
        dawnPlayer.setCurrentMana(dawnPlayer.getMana());
    }
}
