package dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGui;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuiHolder;
import dawn.nitsugua.dawnSurvivalRPG.GUI.GUIRegistries.CompactorRegistry;
import dawn.nitsugua.dawnSurvivalRPG.GUI.GuiButton;
import dawn.nitsugua.dawnSurvivalRPG.GUI.Utils.ExchangeRecipe;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InventoryUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList; 
import java.util.List;
import java.util.Map;

public class CompactorGui extends DawnGui {
    private final DawnSurvivalRPG plugin;
    public CompactorGui(DawnSurvivalRPG plugin) {super("compactor"); this.plugin = plugin;}
    private static final int ITEMS_PER_PAGE = 26;

    @Override
    public Component title(Player viewer) {
        return TextUtil.format("<dark_gray>Compactor");
    }

    @Override
    public int rows() {
        return 6;
    }

    @Override
    public void populate(DawnGuiHolder holder) {
        Player viewer = holder.getViewer();
        int currentPage = holder.getPage();

        holder.fillBorder();

        // 2. Calculate which items to show on this page
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, CompactorRegistry.RECIPES.size());

        int currentSlot = 10;

        for(int i = startIndex; i < endIndex; i++) {
            ExchangeRecipe recipe = CompactorRegistry.RECIPES.get(i);

            ItemStack displayItem = new ItemStack(InventoryUtil.resolveItem(plugin, recipe.icon(), 1));
            displayItem.setItemMeta(holder.itemBuilder(
                    displayItem.getItemMeta(),
                    recipe.displayName(),
                    DawnGuiHolder.parseLore(recipe.lore())
            ));

            holder.setButton(currentSlot, new GuiButton(displayItem, e -> {
                //recipe.action().accept(viewer);
                compactItems(viewer, recipe);

            }));

            currentSlot++;
            if(currentSlot % 9 == 8) {
                currentSlot += 2;
            }
        }

        setupPagination(holder, currentPage);
    }

    private void compactItems(Player viewer, ExchangeRecipe recipe) {
        int have = InventoryUtil.countItems(viewer.getInventory(), recipe.costItemId());
        if(have < recipe.costAmount()) {
            viewer.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You don't have enough items!"));
            return;
        }
        InventoryUtil.removeItems(viewer.getInventory(), recipe.costItemId(), recipe.costAmount());
        ItemStack output = InventoryUtil.resolveItem(plugin, recipe.result(), recipe.resultAmount());
        if(output != null) {
            Map<Integer, ItemStack> leftover = viewer.getInventory().addItem(output);
            for(ItemStack drop : leftover.values()) {
                viewer.getWorld().dropItemNaturally(viewer.getLocation(), drop);
            }
        }
    }

    private void setupPagination(DawnGuiHolder holder, int currentPage) {
        int totalPages = (int) Math.ceil((double) CompactorRegistry.RECIPES.size() / ITEMS_PER_PAGE);

        if(currentPage > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            prev.setItemMeta(holder.itemBuilder(prev.getItemMeta(), "<aqua><- Previous Page", null));
            holder.setButton(45, new GuiButton(prev, e -> {
                holder.setPage(currentPage - 1);
                // Run 1 tick later to prevent client-side ghost items
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("YourPluginName"), holder::refresh);
            }));
        }

        if(currentPage < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            next.setItemMeta(holder.itemBuilder(next.getItemMeta(), "<aqua>Next Page ->", null));
            holder.setButton(53, new GuiButton(next, e -> {
                holder.setPage(currentPage + 1);
                // Run 1 tick later to prevent client-side ghost items
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("YourPluginName"), holder::refresh);
            }));
        }
    }
}
