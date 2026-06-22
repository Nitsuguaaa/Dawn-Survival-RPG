package dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.PlayerManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final PlayerManager playerManager;

    public PlayerEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.playerManager = new PlayerManager(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerManager.playerJoinEvent(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {

    }
}
