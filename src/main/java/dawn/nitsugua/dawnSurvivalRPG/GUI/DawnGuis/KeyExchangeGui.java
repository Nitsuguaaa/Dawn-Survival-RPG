package dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis;

import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGui;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuiHolder;
import dawn.nitsugua.dawnSurvivalRPG.GUI.GuiButton;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KeyExchangeGui extends DawnGui {
    public KeyExchangeGui() {super("key_exchange");}

    @Override
    public Component title(Player viewer) {return TextUtil.format("<aqua>Key Exchange");}

    @Override
    public int rows() {return 3;}

    @Override
    public void populate(DawnGuiHolder holder) {
        Player viewer = holder.getViewer();

        holder.fillBorder();

        ItemStack commonKey = new ItemStack(Material.LIGHT_GRAY_DYE);
        List<String> commonKeyLore = new ArrayList<>();
        commonKeyLore.add("<yellow>Crafting cost:");
        commonKeyLore.add("<white>x10 <gradient:#464958:#FFFFFF>Common key shards");
        commonKeyLore.add("");
        commonKeyLore.add("<gray>Obtain from:");
        commonKeyLore.add("<gray>- Dungeon Mobs");
//        commonKeyLore.add("<gray>- Fishing");
//        commonKeyLore.add("<gray>- Fishing");
        commonKey.setItemMeta(holder.itemBuilder(commonKey.getItemMeta(), "<gradient:#464958:#FFFFFF><b>Common Key", DawnGuiHolder.parseLore(commonKeyLore)));
        holder.setButton(10, new GuiButton(commonKey, e -> {
            viewer.sendMessage(TextUtil.format("<red>Common key not set!"));
        }));


        ItemStack eliteKey = new ItemStack(Material.LIGHT_BLUE_DYE);
        List<String> eliteKeyLore = new ArrayList<>();
        eliteKeyLore.add("<yellow>Crafting cost:");
        eliteKeyLore.add("<white>x25 <gradient:#2DBDC2:#FFFFFF>Elite key shards");
        eliteKeyLore.add("");
        eliteKeyLore.add("<gray>Obtain from:");
        eliteKeyLore.add("<gray>- Dungeon Mobs");
        eliteKey.setItemMeta(holder.itemBuilder(eliteKey.getItemMeta(), "<gradient:#2DBDC2:#FFFFFF><b>Elite Key", DawnGuiHolder.parseLore(eliteKeyLore)));
        holder.setButton(11, new GuiButton(eliteKey, e -> {
            viewer.sendMessage(TextUtil.format("<red>Elite key not set!"));
        }));

    }


}
