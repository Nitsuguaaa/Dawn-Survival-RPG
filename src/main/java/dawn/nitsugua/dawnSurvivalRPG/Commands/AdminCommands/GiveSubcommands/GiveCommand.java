package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.GiveSubcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemFactory;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GiveCommand {

    private final DawnSurvivalRPG plugin;
    private final CustomItemFactory customItemFactory;

    public GiveCommand(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        customItemFactory = new CustomItemFactory(this.plugin);
    }

    @NotNull
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("give")
                .then(Commands.argument("player", StringArgumentType.word())
                        .then(Commands.argument("custom-item", StringArgumentType.word())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            if(!(ctx.getSource().getSender() instanceof Player player)) {
                                                ctx.getSource().getSender().sendMessage("For player's only");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            String playerName = ctx.getArgument("player", String.class);
                                            Player target = plugin.getServer().getPlayer(playerName) == null ? player : plugin.getServer().getPlayer(playerName);

                                            String itemKey = ctx.getArgument("custom-item", String.class);
                                            int amount = ctx.getArgument("amount", int.class);

                                            Inventory inventory = target.getInventory();
                                            //check also if inventory is full
                                            ItemStack finalItem = customItemFactory.createCustomItem(itemKey, amount);

                                            if(finalItem == null) {
                                                target.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Custom item doesn't exist"));
                                                return 0;
                                            }

                                            HashMap<Integer, ItemStack> overflow = inventory.addItem(finalItem);
                                            if(!overflow.isEmpty()) {
                                                for(ItemStack item : overflow.values()) {
                                                    target.getWorld().dropItemNaturally(target.getLocation(), item);
                                                }
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                );

    }
}
