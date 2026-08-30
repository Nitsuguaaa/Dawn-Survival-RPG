package dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis;

import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGui;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuiHolder;
import dawn.nitsugua.dawnSurvivalRPG.GUI.GuiButton;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainMenuGui extends DawnGui {
    public MainMenuGui() {
        super("main_menu");
    }

    @Override
    public Component title(Player viewer) {
        return TextUtil.format("<gold>Dawn SMP");
    }

    @Override
    public int rows() {
        return 3;
    }

    @Override
    public void populate(DawnGuiHolder holder) {
        Player viewer = holder.getViewer();

        ItemStack collectionsIcon = new ItemStack(Material.BOOK);

        holder.setButton(11, new GuiButton(collectionsIcon, event -> {
            viewer.sendMessage(TextUtil.format("<green>Opening Collections!"));
        }));

        ItemStack closeIcon = new ItemStack(Material.BARRIER);
        holder.setButton(15, new GuiButton(closeIcon, event -> {
            viewer.closeInventory();
        }));
    }
}
