package dawn.nitsugua.dawnSurvivalRPG.Listeners.TestListeners;

import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

public class PlayerBreakingBlockListener implements Listener {

    @EventHandler
    public void onBreaking(BlockDamageEvent e) {
        Player player = e.getPlayer();
        //player.sendMessage(Component.text("OOOOHHHH HE BREAKING!"));
        Block block = e.getBlock();

    }
}
