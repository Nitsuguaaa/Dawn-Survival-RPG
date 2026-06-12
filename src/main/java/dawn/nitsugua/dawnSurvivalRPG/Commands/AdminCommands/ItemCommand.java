package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.GiveSubcommands.GiveCommand;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemCommand {
    private final DawnSurvivalRPG plugin;

    public ItemCommand(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void register(@NotNull Commands commands) {
        LiteralCommandNode<CommandSourceStack> dawnrpgNode = Commands.literal("dawnitem")
                .requires(sender -> sender.getSender().isOp()) //only for admin
                .executes(ctx -> {
                    sendPluginInfo(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(new GiveCommand(plugin).build())
                .build();

        commands.register(
                dawnrpgNode,
                "DawnRPG's Item System",
                List.of("di")
        );
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
                        .append(Component.text("/dawnitem give <player> <custom_item> - give the player a DawnRPG item", NamedTextColor.YELLOW))
                //.append(Component.newline())
                //.append(Component.text("/dr test2 test - Nested Command", NamedTextColor.YELLOW))

        );
    }
}
