package dawn.nitsugua.dawnSurvivalRPG.Listeners.GuiListeners;

import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuiHolder;
import dawn.nitsugua.dawnSurvivalRPG.GUI.GuiButton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class GuiEvents implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory top = e.getView().getTopInventory();
        if(!(top.getHolder() instanceof DawnGuiHolder)) return;

        Inventory clicked = e.getClickedInventory();
        if(clicked == null) {
            e.setCancelled(true);
            return;
        }

        if(clicked.getHolder() instanceof DawnGuiHolder guiHolder) {
            e.setCancelled(true);
            GuiButton button = guiHolder.getButton(e.getSlot());
            if(button != null) button.handleClick(e);
            return;
        }

        if(e.isShiftClick() || e.getClick() == ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Inventory top = e.getView().getTopInventory();
        if(!(top instanceof DawnGuiHolder)) return;

        int topSize = top.getSize();
        for(int rawSlot : e.getRawSlots()) {
            if(rawSlot < topSize) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(!(e.getInventory().getHolder() instanceof DawnGuiHolder)) return;
    }
}
