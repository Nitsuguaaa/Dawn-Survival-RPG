package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.GiveSubcommands;

import com.mojang.brigadier.Command;
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
                //Argument 1 <item>
                .then(Commands.literal("static-item")
                    .executes(ctx -> {
                        if(!(ctx.getSource().getSender() instanceof Player player)) {
                            ctx.getSource().getSender().sendMessage("For player's only");
                            return Command.SINGLE_SUCCESS;
                        }

                        /*
                            This will be removed once Custom Item is finished

                            TODO:
                                - remove static custom item and replace with item factory
                                - Fix attribute shit IDK
                         */
                        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Running give command"));

                        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
                        Inventory inventory = player.getInventory();

                        ItemMeta itemMeta = item.getItemMeta();

                        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                        itemMeta.displayName(TextUtil.setItemRarity(TextUtil.ItemRarity.RARE, "End Sword"));
                        AttributeModifier newAttr = new AttributeModifier(
                                NamespacedKey.minecraft("base_attack_damage"),
                                9,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND
                        );
                        itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, newAttr);
                        item.setItemMeta(itemMeta);


                        inventory.addItem(item);
                        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Gave you x" + item.getAmount() + " " + PlainTextComponentSerializer.plainText().serialize(item.displayName())));

                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("info")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    if(!(ctx.getSource().getSender() instanceof Player player)) {
                                        ctx.getSource().getSender().sendMessage("For player's only");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    String itemKey = ctx.getArgument("name", String.class);
                                    Inventory inventory = player.getInventory();

                                    inventory.addItem(customItemFactory.createCustomItem(itemKey, 1));

                                    //CustomItemFactory.createCustomItem(itemKey);

                                    /*CustomItem customItem = customItemManager.getCustomItem(itemKey);
                                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "You are trying to get: " + itemKey));
                                    if(customItem == null) {
                                        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Custom Item not found!"));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Taking info: " + customItem.getItemName() +
                                            "\n rarity: " + customItem.getRarity() +
                                            "\n category: " + customItem.getCategory() +
                                            "\n sub-category: " + customItem.getSubCategory() +
                                            "\n material:" + customItem.getCustomMaterial() +
                                            "\n parent-material: " + customItem.getParentMaterial().name() +
                                            "\n lore:"));

                                    for(Component loreLine : customItem.getLore()) {
                                        player.sendMessage(loreLine);
                                    }*/

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        /*.executes(ctx -> {

                        })*/
                )
                //Runs if item is not stated
                .executes(ctx -> {
                    if(!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("For player's only");
                        return Command.SINGLE_SUCCESS;
                    }
                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Please provide an item!"));
                    return Command.SINGLE_SUCCESS;
                });
    }
}
