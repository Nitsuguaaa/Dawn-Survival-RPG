package dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.PartyManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;


public class PlayerPartyEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final PartyManager partyManager;

    public PlayerPartyEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.partyManager = plugin.getPartyManager();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPartyDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player victim)) return;

        Player attacker = getAttacker(e.getDamager());
        if(attacker == null) return;

        if(attacker.equals(victim)) return;

        if(partyManager.isTheSameParty(attacker, victim)) {
            e.setCancelled(true);
        }
    }

    private Player getAttacker(Entity damager) {
        if(damager instanceof Player p) return p;

        if(damager instanceof Projectile proj) {
            ProjectileSource shooter = proj.getShooter();
            if(shooter instanceof Player p) return p;
        }

        if(damager instanceof TNTPrimed tnt) {
            Entity source = tnt.getSource();
            if(source instanceof Player p) return p;
        }

        if(damager instanceof AreaEffectCloud cloud) {
            ProjectileSource source = cloud.getSource();
            if(source instanceof Player p) return p;
        }

        return null;
    }

}
