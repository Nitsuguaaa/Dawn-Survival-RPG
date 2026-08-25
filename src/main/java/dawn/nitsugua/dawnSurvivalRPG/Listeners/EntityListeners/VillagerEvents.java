package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemFactory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class VillagerEvents implements Listener {

    private final DawnSurvivalRPG plugin;
    private final CustomItemFactory customItemFactory;


    public VillagerEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.customItemFactory = new CustomItemFactory(this.plugin);
    }

    @EventHandler
    public void onTradeAcquire(VillagerAcquireTradeEvent e) {
        MerchantRecipe recipe = e.getRecipe();

        if(recipe.getResult().getType() != Material.ENCHANTED_BOOK) return;
        if(!(recipe.getResult().getItemMeta() instanceof EnchantmentStorageMeta bookMeta)) return;

        //e.setCancelled(true);


        ItemStack endSword = customItemFactory.createCustomItem("END_SWORD", 1);
        MerchantRecipe replacement = new MerchantRecipe(endSword, 12);
        replacement.addIngredient(new ItemStack(Material.DIAMOND, 10));

        replacement.setPriceMultiplier(0.05f);

        e.setRecipe(replacement);
    }
}
