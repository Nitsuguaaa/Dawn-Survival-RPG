package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.OverworldManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChunkCommand {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dr = DawnRegistry.getInstance();

    public ChunkCommand(DawnSurvivalRPG plugin) {this.plugin = plugin;}

    public void register(@NotNull Commands commands) {
        LiteralCommandNode<CommandSourceStack> dawnrpgNode = Commands.literal("dawnchunk")
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx -> {
                    sendPluginInfo(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("update")
                        .then(Commands.argument("key", StringArgumentType.string())
                                .then(Commands.argument("min-y", IntegerArgumentType.integer(-63))
                                        .then(Commands.argument("max-y", IntegerArgumentType.integer(-63))
                                                .executes(ctx -> {
                                                    String key = ctx.getArgument("key", String.class);
                                                    int minY = ctx.getArgument("min-y", Integer.class);
                                                    int maxY = ctx.getArgument("max-y", Integer.class);

                                                    DawnRegistry.getInstance().setStructureYRange(key, new int[]{minY, maxY});

                                                    int ramUpdated = DawnRegistry.getInstance().updateYRangeForStructure(key, minY, maxY);

                                                    plugin.getChunkDAO().updateYRangeByStructureAsync(key, minY, maxY).thenAccept(dbUpdated -> {
                                                        ctx.getSource().getSender().sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "<green>Updated Y-range for " + key + " to [" + minY + ", " + maxY + "]"));
                                                        ctx.getSource().getSender().sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,"<gray>RAM chunks updated: " + ramUpdated + " | DB rows updated: " + dbUpdated));
                                                    });

                                                    OverworldManager.updateConfig(plugin, key, new int[]{minY, maxY});
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
                .build();

        commands.register(dawnrpgNode, "DawnRPG's Sell System", List.of("dc"));
    }

    /**
     * Sends plugin information to command sender
     *
     * @param source The command source/sender
     */
    private void sendPluginInfo(@NotNull CommandSourceStack source) {
        source.getSender().sendMessage(
                Component.text("========================================", NamedTextColor.DARK_GRAY)
                        .append(Component.newline())
                        .append(Component.text("    DawnRPG  ", NamedTextColor.GREEN))
                        .append(Component.text("v" + plugin.getPluginMeta().getVersion(), NamedTextColor.GREEN))
                        .append(Component.newline())
                        .append(Component.text("========================================", NamedTextColor.DARK_GRAY))
                        .append(Component.newline())
                        .append(Component.text("Commands: ", NamedTextColor.WHITE))
                        .append(Component.newline())
                        .append(Component.text("/sell reload - reloads the config", NamedTextColor.YELLOW))
                //.append(Component.newline())
                //.append(Component.text("/dr test2 test - Nested Command", NamedTextColor.YELLOW))

        );
    }
}
