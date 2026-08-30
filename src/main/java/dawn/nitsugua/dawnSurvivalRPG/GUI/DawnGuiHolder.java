package dawn.nitsugua.dawnSurvivalRPG.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public class DawnGuiHolder implements InventoryHolder {
    private final DawnGui gui;
    private final Player viewer;
    private final Inventory inventory;
    private final Map<Integer, GuiButton> buttons = new HashMap<>();
    private int page = 0;

    public DawnGuiHolder(DawnGui gui, Player viewer) {
        this.gui = gui;
        this.viewer = viewer;

        Component title = gui.title(viewer);
        int size = Math.max(1, Math.min(6, gui.rows()) * 9);
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public DawnGui getGui() { return gui; }
    public Player getViewer() { return viewer; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public void setButton(int slot, GuiButton button) {
        if (button == null) return;
        buttons.put(slot, button);
        inventory.setItem(slot, button.getIcon());
    }

    public GuiButton getButton(int slot) {
        return buttons.get(slot);
    }

    public void clearButtons() {
        buttons.clear();
        inventory.clear();
    }
}
