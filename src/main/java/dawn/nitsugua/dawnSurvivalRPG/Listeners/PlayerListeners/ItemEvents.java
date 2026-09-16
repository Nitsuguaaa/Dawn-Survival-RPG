package dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.CooldownManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.ManaManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.SkillManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DebounceUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.event.player.PlayerLoomPatternSelectEvent;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

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

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        AnvilInventory inv = e.getInventory();

        ItemStack target = inv.getItem(0);
        if(target == null) return;

        ItemStack sacrifice = inv.getItem(1);

        boolean targetIsDawn = DawnKeys.isDawnObject(target);
        boolean sacrificeIsDawn = DawnKeys.isDawnObject(sacrifice);

        //check if the item is a dawn object, if it's not equipment related then don't bother
        //TODO: if decided to make custom enchants then check for books as well but that's for the future
        if(targetIsDawn && target.getType().getMaxDurability() <= 0) {
            e.setResult(null);
            return;
        }

        if(sacrificeIsDawn && sacrifice.getType().getMaxDurability() <= 0) {
            e.setResult(null);
            return;
        }

        //ban custom items and vanilla items with the same material type
        if(targetIsDawn && !(sacrificeIsDawn)) {
            e.setResult(null);
        }
        if(sacrificeIsDawn && !(targetIsDawn)) {
            e.setResult(null);
        }

        if(e.getResult() == null) return;
        //disable renaming
        // 3. Disable Renaming (The Fix!)
        // Only compare the CUSTOM_NAME component (the text the player typed in the box).
        // If they didn't type anything, both are null/unchanged, and Objects.equals() returns true!
        Component targetCustomName = target.getData(DataComponentTypes.CUSTOM_NAME);
        Component resultCustomName = e.getResult().getData(DataComponentTypes.CUSTOM_NAME);

        if (!Objects.equals(targetCustomName, resultCustomName) && targetIsDawn) {
            // The player tried to type a new name in the text box!
            e.setResult(null);
        }

    }

    @EventHandler
    public void onStonecutterPrepare(PlayerStonecutterRecipeSelectEvent e) {
        Inventory inv = e.getStonecutterInventory();
        if(DawnKeys.isDawnObject(inv.getItem(0))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLoomPrepare(PlayerLoomPatternSelectEvent e) {
        Inventory inv = e.getLoomInventory();
        if (DawnKeys.isDawnObject(inv.getItem(0))
                || DawnKeys.isDawnObject(inv.getItem(1))
                || DawnKeys.isDawnObject(inv.getItem(2))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (!(top instanceof FurnaceInventory) && !(top instanceof BrewerInventory)) return;

        Inventory clicked = e.getClickedInventory();
        if (clicked == null) return; // Clicked outside the window

        Player player = (Player) e.getWhoClicked();

        // --- 1. CATCH NUMBER KEY SWAPS (Hotbar 1-9) ---
        if (e.getClick() == ClickType.NUMBER_KEY) {
            // Get the actual item in the hotbar slot being swapped
            ItemStack hotbarItem = player.getInventory().getItem(e.getHotbarButton());
            if (DawnKeys.isDawnObject(hotbarItem)) {
                e.setCancelled(true); // Block it from entering the top inventory
            }
            return;
        }

        boolean holdingDawnItem = DawnKeys.isDawnObject(e.getCursor());
        boolean clickingDawnItem = DawnKeys.isDawnObject(e.getCurrentItem());
        boolean isPlayerInv = (clicked == player.getInventory());

        // --- 2. CATCH SHIFT-CLICKS ---
        if (e.getClick().isShiftClick()) {
            // Shift-clicking from player inventory sends it to the top inventory
            if (isPlayerInv && clickingDawnItem) {
                e.setCancelled(true);
            }
            return;
        }

        // --- 3. CATCH DOUBLE CLICKS (Collection) ---
        if (e.getClick() == ClickType.DOUBLE_CLICK) {
            if (clickingDawnItem || holdingDawnItem) {
                e.setCancelled(true);
            }
            return;
        }

        // --- 4. CATCH STANDARD CLICKS (Left, Right, Drop, etc.) ---
        // If holding a Dawn item and clicking on the top inventory -> BLOCK placement
        if (holdingDawnItem && clicked == top) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (!(top instanceof FurnaceInventory) && !(top instanceof BrewerInventory)) return;

        if (DawnKeys.isDawnObject(e.getOldCursor())) {
            int topSize = top.getSize();
            for (int rawSlot : e.getRawSlots()) {
                if (rawSlot < topSize) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }



    private boolean isDawnRecipe(Recipe recipe) {
        if(!(recipe instanceof Keyed keyed)) return false;

        NamespacedKey key = keyed.getKey();
        return key.getNamespace().equalsIgnoreCase("DawnSurvivalRPG");
    }
}
