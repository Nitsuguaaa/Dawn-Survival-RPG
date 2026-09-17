package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.PartyManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PartyCommand {
    private final DawnSurvivalRPG plugin;
    private final PartyManager partyManager;

    public PartyCommand(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.partyManager = plugin.getPartyManager();
    }

    public void register(@NotNull Commands commands) {
        LiteralCommandNode<CommandSourceStack> dawnrpgNode = Commands.literal("party")
                .executes(ctx -> {
                    sendPluginInfo(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("create")
                        .then(Commands.argument("party-name", StringArgumentType.word())
                                .executes(ctx -> {
                                    if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                    partyManager.createParty(player, ctx.getArgument("party-name", String.class));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("invite")
                        .then(Commands.argument("player-name", StringArgumentType.word())
                                .executes(ctx -> {
                                    if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                    Player invited = Bukkit.getPlayer(ctx.getArgument("player-name", String.class));
                                    if(invited == null) {
                                        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Player doesn't exist or is offline!"));
                                        return 0;
                                    }
                                    partyManager.invitePlayer(player, invited);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("accept")
                        .then(Commands.argument("player-name", StringArgumentType.word())
                                .executes(ctx -> {
                                    if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                    Player inviter = Bukkit.getPlayer(ctx.getArgument("player-name", String.class));
                                    if(inviter == null) {
                                        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Player doesn't exist!"));
                                        return 0;
                                    }
                                    partyManager.acceptInvite(player, inviter);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("decline")
                        .then(Commands.argument("player-name", StringArgumentType.word())
                                .executes(ctx -> {
                                    if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                    Player inviter = Bukkit.getPlayer(ctx.getArgument("player-name", String.class));
                                    if(inviter == null) return 0;
                                    partyManager.declineInvite(player, inviter);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("player-name", StringArgumentType.word())
                                .executes(ctx -> {
                                    if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                    partyManager.removeMember(player, ctx.getArgument("player-name", String.class));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("leave")
                        .executes(ctx -> {
                            if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                            partyManager.leaveParty(player);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("disband")
                        .executes(ctx -> {
                            if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                            partyManager.disbandParty(player);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();

        commands.register(dawnrpgNode, "DawnRPG's Party System", List.of("dp"));
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
                        .append(Component.text("/party create <name>", NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("/party leave", NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("/party accept <inviter-name>", NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("/party decline <inviter-name>", NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("/party disband - leader only", NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("/party invite <player-name> - leader only", NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("/party remove <player-name> - leader only", NamedTextColor.YELLOW))
                //.append(Component.newline())
                //.append(Component.text("/dr test2 test - Nested Command", NamedTextColor.YELLOW))

        );
    }
}
