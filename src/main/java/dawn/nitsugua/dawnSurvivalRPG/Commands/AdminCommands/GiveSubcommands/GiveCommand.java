package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.GiveSubcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.keys.ItemTypeKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class GiveCommand {

    private final DawnSurvivalRPG plugin;

    public GiveCommand(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("give")
                //Argument 1 <item>
                .then(Commands.argument("item", StringArgumentType.word())
                    .executes(ctx -> {
                        if(!(ctx.getSource().getSender() instanceof Player player)) {
                            ctx.getSource().getSender().sendMessage("For player's only");
                            return Command.SINGLE_SUCCESS;
                        }

                        String commandItem = ctx.getArgument("item", String.class);

                        player.sendMessage(Component.text("You said: " + commandItem));

                        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
                        Inventory inventory = player.getInventory();

                        ItemMeta itemMeta = item.getItemMeta();

                        itemMeta.customName(Component.text("End Sword"));
                        item.setItemMeta(itemMeta);

                        inventory.addItem(item);
                        player.sendMessage(Component.text("Gave you 1 Diamond Sword"));

                        return Command.SINGLE_SUCCESS;
                    })
                )
                //Runs if item is not stated
                .executes(ctx -> {
                    if(!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("For player's only");
                        return Command.SINGLE_SUCCESS;
                    }
                    ctx.getSource().getExecutor().sendMessage(Component.text("You did not provide an item."));
                    return Command.SINGLE_SUCCESS;
                });
    }
}
