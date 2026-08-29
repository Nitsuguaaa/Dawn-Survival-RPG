package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.DungeonSubcommands.AddNodeCommand;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.DungeonSubcommands.CreateCommand;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.DungeonSubcommands.StageCommand;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.GiveSubcommands.GiveCommand;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DungeonCommand {
    private final DawnSurvivalRPG plugin;

    public DungeonCommand(DawnSurvivalRPG plugin) {this.plugin = plugin;}

    public void register(@NotNull Commands commands) {
        LiteralCommandNode<CommandSourceStack> dawnrpgNode = Commands.literal("dawndungeon")
                .requires(sender -> sender.getSender().isOp()) //only for admin
                .executes(ctx -> {
                    sendPluginInfo(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(new CreateCommand(plugin).build())
                .then(new AddNodeCommand(plugin).build())
                .then(new StageCommand(plugin).build())
                .build();

        commands.register(
                dawnrpgNode,
                "DawnRPG's Item System",
                List.of("dd")
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
                        .append(Component.text("/dawndungeon create <name> <mob> <max spawn per interval> - create a dawn spawner", NamedTextColor.YELLOW))
                //.append(Component.newline())
                //.append(Component.text("/dr test2 test - Nested Command", NamedTextColor.YELLOW))

        );
    }
}
