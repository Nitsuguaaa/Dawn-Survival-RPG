package dawn.nitsugua.dawnSurvivalRPG.GUI;

import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void setDecorationIcon(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    public void fillBorder() {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta itemMeta = filler.getItemMeta();
        itemMeta.setHideTooltip(true);
        filler.setItemMeta(itemMeta);

        int size = inventory.getSize();
        int columns = 9;
        int rows = size/columns;

        for(int slot = 0; slot < size; slot++) {
            int col = slot % columns;
            int row = slot / columns;

            boolean isTopRow = (row == 0);
            boolean isBottomRow = (row == rows - 1);
            boolean isLeftCol = (col == 0);
            boolean isRightCol = (col == columns - 1);

            if (isTopRow || isBottomRow || isLeftCol || isRightCol) {
                inventory.setItem(slot, filler);
            }
        }
    }

//    private ItemStack removeAttributes(ItemStack item) {
//
//    }

    public ItemMeta itemBuilder(ItemMeta meta, String displayName, @Nullable List<Component> description) {
        meta.displayName(TextUtil.format(displayName).decoration(TextDecoration.ITALIC, false));
        //meta.setHideTooltip(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);

        if(description != null) {
            meta.lore(description);
        }

        return meta;
    }

    //Method might change class if usability is frequent.
    public static List<Component> parseLore(List<String> lore) {
        List<Component> componentLore = new ArrayList<>();

        for(String line : lore) {
            if(line == null || line.isEmpty()) {
                componentLore.add(Component.empty());
            } else {
                componentLore.add(TextUtil.setItemLore(line));
            }
        }

        return componentLore;
    }

    public GuiButton getButton(int slot) {
        return buttons.get(slot);
    }

    public void clearButtons() {
        buttons.clear();
        inventory.clear();
    }
}
