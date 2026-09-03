package dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis;

import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGui;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuiHolder;
import dawn.nitsugua.dawnSurvivalRPG.GUI.GuiButton;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InventoryUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CompactorGui extends DawnGui {
    public CompactorGui() {super("compactor");}

    @Override
    public Component title(Player viewer) {
        return TextUtil.format("<gold>Compactor");
    }

    @Override
    public int rows() {
        return 6;
    }

    @Override
    public void populate(DawnGuiHolder holder) {
        Player viewer = holder.getViewer();

        holder.fillBorder();
    }
}
