package dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis;

import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGui;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuiHolder;
import dawn.nitsugua.dawnSurvivalRPG.GUI.GuiButton;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MainMenuGui extends DawnGui {
    public MainMenuGui() {
        super("main_menu");
    }

    @Override
    public Component title(Player viewer) {
        return TextUtil.format("<dark_gray>Dawn SMP");
    }

    @Override
    public int rows() {
        return 3;
    }

    @Override
    public void populate(DawnGuiHolder holder) {
        Player viewer = holder.getViewer();

        ItemStack SERVER_INFO = new ItemStack(Material.BOOK);
        SERVER_INFO.setItemMeta(holder.itemBuilder(
                SERVER_INFO.getItemMeta(),
                "Server info",
                null
        ));
        holder.setButton(13, new GuiButton(SERVER_INFO, event -> {
            viewer.closeInventory();
            MessagesUtil.sendServerInfo(viewer);
        }));

//        ItemStack closeIcon = new ItemStack(Material.BARRIER);
//
//        holder.setButton(15, new GuiButton(closeIcon, event -> {
//            viewer.closeInventory();
//        }));
    }
}
