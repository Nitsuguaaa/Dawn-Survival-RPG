package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.DungeonSubcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.DungeonManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StageCommand {
    private final DawnSurvivalRPG plugin;
    private final DungeonManager dungeonManager;

    public StageCommand(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.dungeonManager = plugin.getDungeonManager();
    }

    @NotNull
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("stage")
                .then(Commands.argument("spawner-name", StringArgumentType.word())
                        .executes(ctx -> {
                            if(!(ctx.getSource().getSender() instanceof Player player)) {
                                ctx.getSource().getSender().sendMessage(TextUtil.format("<red>For player's only"));
                                return 0;
                            }

                            String name = ctx.getArgument("spawner-name", String.class);
                            dungeonManager.toggleStage(player, name);
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
