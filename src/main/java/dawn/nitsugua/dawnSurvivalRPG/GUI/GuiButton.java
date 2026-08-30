package dawn.nitsugua.dawnSurvivalRPG.GUI;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiButton {
    private final ItemStack icon;
    private final Consumer<InventoryClickEvent> onClick;

    public GuiButton(ItemStack icon, Consumer<InventoryClickEvent> onClick) {
        this.icon = icon;
        this.onClick = onClick;
    }

    public ItemStack getIcon() {return icon;}

    public void handleClick(InventoryClickEvent e) {
        if(onClick != null) onClick.accept(e);
    }
}
