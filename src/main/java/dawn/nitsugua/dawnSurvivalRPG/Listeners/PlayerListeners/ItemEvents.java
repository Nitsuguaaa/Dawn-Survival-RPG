package dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.CooldownManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.ManaManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.SkillManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DebounceUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final ManaManager manaManager;
    private final CooldownManager cooldownManager;

    public ItemEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.manaManager = plugin.getManaManager();
        this.cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler
    public void onSkillUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(!DawnKeys.isDawnObject(item)) return;

        // Ensure we are only firing skills on right clicks
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        if(!pdc.has(DawnKeys.USES)) {return;}

        boolean isActiveSkill = pdc.has(DawnKeys.MANA) && pdc.has(DawnKeys.COOLDOWN);
        // 2. The ONLY exception: Block Concentrated Eyes from going into End Portals
        boolean isBlockedVanillaPlacement = false;
        if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
            if (item.getType() == Material.ENDER_EYE) {
                isBlockedVanillaPlacement = true;
            }
        }

        // 3. SMART CANCELLATION
        if (isActiveSkill || isBlockedVanillaPlacement) {
            e.setCancelled(true); // Hijack the click!
        } else {
            // It's a passive material (like an Elite Key Shard).
            // Let vanilla Minecraft handle opening chests, doors, etc.!
            return;
        }

        // GLOBAL DEBOUNCE: Stops all skill uses for 200ms after any skill
        if (DebounceUtil.isGlobalDebounceActive(player.getUniqueId())) {
            return; // Too fast! Ignore the click
        }

        if (isActiveSkill) {
            if (!manaManager.hasMana(player, pdc)) return;

            if (cooldownManager.isOnCooldown(player, item)) return;

            plugin.getSkillManager().onUse(player, item, pdc);
        }
    }

    @EventHandler
    public void customBlockPlacement(BlockPlaceEvent e) {
        if(DawnKeys.isDawnObject(e.getItemInHand())) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        int currentSlot = e.getNewSlot();
        int previousSlot = e.getPreviousSlot();
        ItemStack item = player.getInventory().getItem(currentSlot);
        ItemStack previousItem = player.getInventory().getItem(previousSlot);

        if(item == null && previousItem == null) {return;}

        manaManager.calculateMaxIntelligenceFromItem(player, item, previousItem);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        if(DawnKeys.isDawnObject(e.getInventory().getResult())) return;
        for(ItemStack ingredient : e.getInventory().getMatrix()) {
            if(ingredient == null || ingredient.getType() == Material.AIR) continue;

            if(DawnKeys.isDawnObject(ingredient)) {
                e.getInventory().setResult(null);
                return;
            }
        }
    }

    @EventHandler
    public void onPrepareSmith(PrepareSmithingEvent e) {
        SmithingInventory inv = e.getInventory();
        ItemStack result = e.getResult();
        if(result == null) return;

        Recipe recipe = inv.getRecipe();
        if(isDawnRecipe(recipe)) {
            return;
        }

        // Block Dawn items from vanilla recipes
        ItemStack template = inv.getItem(0);
        ItemStack base = inv.getItem(1);
        ItemStack addition = inv.getItem(2);

        if (DawnKeys.isDawnObject(template)
                || DawnKeys.isDawnObject(base)
                || DawnKeys.isDawnObject(addition)) {
            e.setResult(null);
        }
    }

    private boolean isDawnRecipe(Recipe recipe) {
        if(!(recipe instanceof Keyed keyed)) return false;

        NamespacedKey key = keyed.getKey();
        return key.getNamespace().equalsIgnoreCase("DawnSurvivalRPG");
    }
}
