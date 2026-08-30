package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.GiveSubcommands.GiveCommand;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis.MainMenuGui;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuCommand {
    private final DawnSurvivalRPG plugin;

    public MenuCommand(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void register(@NotNull Commands commands) {
        LiteralCommandNode<CommandSourceStack> dawnrpgNode = Commands.literal("menu")
                //.requires(sender -> sender.getSender().isOp()) //only for admin
                .executes(ctx -> {
                    openMenu(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .build();

        commands.register(
                dawnrpgNode,
                "DawnRPG's Menu System",
                List.of("dm")
        );
    }

    /**
     * Sends plugin information to command sender
     *
     * @param source The command source/sender
     */
    private void openMenu(@NotNull CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) return;
        plugin.getGuiManager().open(player, new MainMenuGui());
    }
}
