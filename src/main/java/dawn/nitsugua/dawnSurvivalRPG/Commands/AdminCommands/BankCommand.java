package dawn.nitsugua.dawnSurvivalRPG.Commands.AdminCommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

import java.util.List;

public class BankCommand {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dr = DawnRegistry.getInstance();

    public BankCommand(DawnSurvivalRPG plugin) {this.plugin = plugin;}

    public void register(@NotNull Commands commands) {
        LiteralCommandNode<CommandSourceStack> dawnrpgNode = Commands.literal("bank")
                .executes(ctx -> {
                    sendPluginInfo(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("deposit")
                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.1))
                                .executes(ctx -> executeDeposit(ctx.getSource(), ctx.getArgument("amount", Double.class)))
                        )
                )
                .then(Commands.literal("withdraw")
                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.1))
                                .executes(ctx -> executeWithdraw(ctx.getSource(), ctx.getArgument("amount", Double.class)))
                        )
                )
                .then(Commands.literal("balance")
                        .executes(ctx -> executeBalance(ctx.getSource()))
                )
                .build();

        commands.register(dawnrpgNode, "DawnRPG's Bank System", List.of("dawnbank"));
    }

    private int executeDeposit(CommandSourceStack source, double amount) {
        if(!(source.getSender() instanceof Player player)) return 0;
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        if(dp == null || dp.getBank() == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Your data is still loading."));
            return 0;
        }

        ExcellentEconomyAPI eco = plugin.getEconomyAPI().getEconomyAPI();
        double walletBalance = eco.getBalance(player, "money");

        if(walletBalance < amount) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Not enough wallet balance!"));
            return 0;
        }

        eco.withdraw(player, "money", amount);
        double newBalance = dp.getBank().getBalance() + amount;
        dp.getBank().setBalance(newBalance);

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Deposited <yellow>₱" + amount + "</yellow>, Bank: <white>₱" + dp.getBank().getBalance()));

        return Command.SINGLE_SUCCESS;
    }

    private int executeWithdraw(CommandSourceStack source, double amount) {
        if(!(source.getSender() instanceof Player player)) return 0;
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        if(dp == null || dp.getBank() == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Your data is still loading."));
            return 0;
        }

        if(dp.getBank().getBalance() < amount) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Not enough bank balance!"));
            return 0;
        }

        double newBalance = dp.getBank().getBalance() - amount;
        dp.getBank().setBalance(newBalance);

        ExcellentEconomyAPI eco = plugin.getEconomyAPI().getEconomyAPI();
        eco.deposit(player, "money", amount);

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Withdrew <yellow>₱" + amount + "</yellow>, Bank: <white>₱" + dp.getBank().getBalance()));
        return Command.SINGLE_SUCCESS;
    }

    private int executeBalance(CommandSourceStack source) {
        if(!(source.getSender() instanceof Player player)) return 0;
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        if(dp == null || dp.getBank() == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Your data is still loading."));
            return 0;
        }

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Bank Balance: <white>₱" + dp.getBank().getBalance()));
        return Command.SINGLE_SUCCESS;
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
                        .append(Component.text("/bank deposit <amount>", NamedTextColor.YELLOW))
                        .append(Component.text("/bank withdraw <amount>", NamedTextColor.YELLOW))
                        .append(Component.text("/bank balance", NamedTextColor.YELLOW))
                //.append(Component.newline())
                //.append(Component.text("/dr test2 test - Nested Command", NamedTextColor.YELLOW))

        );
    }
}
