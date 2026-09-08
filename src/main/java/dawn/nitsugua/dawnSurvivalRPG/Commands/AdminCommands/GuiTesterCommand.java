package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis.CompactorGui;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis.MainMenuGui;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiTesterCommand {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dr = DawnRegistry.getInstance();

    public GuiTesterCommand(DawnSurvivalRPG plugin) {this.plugin = plugin;}

    public void register(@NotNull Commands commands) {
        LiteralCommandNode<CommandSourceStack> dawnrpgNode = Commands.literal("dawngui")
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx -> {
                    sendPluginInfo(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("run")
                        .executes(ctx -> {
                            openMenu(ctx.getSource());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();

        commands.register(dawnrpgNode, "DawnRPG's Sell System", List.of("dg"));
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

    /**
     * Sends plugin information to command sender
     *
     * @param source The command source/sender
     */
    private void openMenu(@NotNull CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) return;
        plugin.getGuiManager().open(player, new CompactorGui(plugin));
    }
}
