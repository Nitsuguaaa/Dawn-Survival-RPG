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
            //viewer.sendMessage(TextUtil.format("<red>Common key not set!"));
            int shards = InventoryUtil.countCustomItems(viewer.getInventory(), "COMMON_KEY_SHARD");

            if(shards >= 10) {
                InventoryUtil.removeCustomItems(viewer.getInventory(), "COMMON_KEY_SHARD", 10);
                String command = String.format("crates key give %s common 1", viewer.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                viewer.sendMessage(TextUtil.format("<green>Successfully crafted a <b><gradient:#464958:#FFFFFF>Common Key</gradient>!"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);

            } else {
                viewer.sendMessage(TextUtil.format("<red>You need <white>10 <red>Common Key Shards! (You have <white>" + shards + "<red>)"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
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
            //viewer.sendMessage(TextUtil.format("<red>Elite key not set!"));\
            int shards = InventoryUtil.countCustomItems(viewer.getInventory(), "ELITE_KEY_SHARD");

            if(shards >= 25) {
                InventoryUtil.removeCustomItems(viewer.getInventory(), "ELITE_KEY_SHARD", 10);
                String command = String.format("crates key give %s elite 1", viewer.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                viewer.sendMessage(TextUtil.format("<green>Successfully crafted a <b><gradient:#464958:#FFFFFF>Common Key</gradient>!"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);

            } else {
                viewer.sendMessage(TextUtil.format("<red>You need <white>10 <red>Elite Key Shards! (You have <white>" + shards + "<red>)"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }));

    }


}
