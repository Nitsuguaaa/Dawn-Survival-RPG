package dawn.nitsugua.dawnSurvivalRPG.Listeners.TestListeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerBreakingBlockListener implements Listener {

    @EventHandler
    public void onBreaking(BlockDamageEvent e) {
        Player player = e.getPlayer();
        //player.sendMessage(Component.text("OOOOHHHH HE BREAKING!"));
        Block block = e.getBlock();

    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player player)) {
            return;
        }
        //player.sendMessage(Component.text("Damage: " + e.getDamage()));
        //player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Removing ticks"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.getAttribute(Attribute.ATTACK_SPEED) == null) {
            return;
        }
    }
}
