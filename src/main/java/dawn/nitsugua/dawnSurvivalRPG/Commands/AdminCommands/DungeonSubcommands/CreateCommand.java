package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands.DungeonSubcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class CreateCommand {
    private final DawnSurvivalRPG plugin;
    private final DungeonManager dungeonManager;

    public CreateCommand(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.dungeonManager = new DungeonManager(this.plugin);
    }

    @NotNull
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.word())
                        .then(Commands.argument("dawn-mob", StringArgumentType.word())
                                .then(Commands.argument("max-spawn", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            if(!(ctx.getSource().getSender() instanceof Player player)) {
                                                ctx.getSource().getSender().sendMessage(TextUtil.format("<red>For player's only"));
                                                return 0;
                                            }

                                            dungeonManager.createDungeon(player, ctx.getArgument("name", String.class), ctx.getArgument("dawn-mob", String.class), ctx.getArgument("max-spawn", Integer.class));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                );

    }
}
