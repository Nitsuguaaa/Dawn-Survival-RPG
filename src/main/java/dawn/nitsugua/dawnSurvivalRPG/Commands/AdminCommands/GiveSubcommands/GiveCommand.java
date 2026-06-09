package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.GiveSubcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
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
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(
                            Component.text("Giving something")
                    );
                    return Command.SINGLE_SUCCESS;
                });
    }
}
