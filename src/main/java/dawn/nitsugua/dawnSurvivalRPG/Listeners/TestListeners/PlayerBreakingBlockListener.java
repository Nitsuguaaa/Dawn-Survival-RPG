package dawn.nitsugua.dawnSurvivalRPG.Listeners.TestListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Economy.Bank;
import dawn.nitsugua.dawnSurvivalRPG.ExcellentEconomy.DawnExcellentEconomy;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

public class PlayerBreakingBlockListener implements Listener {
    private final DawnSurvivalRPG plugin;

    public PlayerBreakingBlockListener(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreaking(BlockBreakEvent e) {
        Player player = e.getPlayer();
        //player.sendMessage(TextUtil.format("<green>I see that your breaking"));
        Block block = e.getBlock();

//        DawnExcellentEconomy wrapper = plugin.getEconomyAPI();
//        if(wrapper == null || !wrapper.isReady()) return;
//
//        ExcellentEconomyAPI api = wrapper.getEconomyAPI();
//        double balance = api.getBalance(player, "money");
//        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Your current balance is: " + balance));

        DawnPlayer dp = DawnRegistry.getInstance().getDawnPlayer(player.getUniqueId());

        if(dp == null || dp.getBank() == null) {
            player.sendMessage("Hold up, database still loading");
            e.setCancelled(true);
            return;
        }

        Bank bank = dp.getBank();

        bank.setBalance(bank.getBalance() + 10.0);

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "+10$ Bank deposit! New Balance: " + bank.getBalance()));

    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player player)) return;
        if(!(e.getEntity() instanceof LivingEntity target)) return;

//        target.setMaximumNoDamageTicks(0);
//        target.setNoDamageTicks(0);
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
