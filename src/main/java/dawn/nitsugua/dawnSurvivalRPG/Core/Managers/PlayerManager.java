package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final DawnSurvivalRPG plugin;

    private final double BASE_MANA = 100.0;

    public PlayerManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void playerJoinEvent(Player player) {
        UUID playerId = player.getUniqueId();
        DawnPlayer dawnPlayer = new DawnPlayer(playerId, BASE_MANA);
    }
}
