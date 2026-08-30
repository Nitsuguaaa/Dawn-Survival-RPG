package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGui;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuiHolder;
import org.bukkit.entity.Player;

public class GuiManager {
    public void open(Player player, DawnGui gui) {
        DawnGuiHolder holder = new DawnGuiHolder(gui, player);
        gui.populate(holder);
        player.openInventory(holder.getInventory());
    }

    public void refresh(DawnGuiHolder holder) {
        holder.clearButtons();
        holder.getGui().populate(holder);
    }
}
