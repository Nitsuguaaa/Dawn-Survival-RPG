package dawn.nitsugua.dawnSurvivalRPG.placeholders;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Locale;

public class DawnBankPlaceholder extends PlaceholderExpansion {
    private final DawnSurvivalRPG plugin;
    private final NumberFormat formatter;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public DawnBankPlaceholder(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        // Format numbers with commas (e.g., 1,000,000 instead of 1000000.0)
        this.formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        //plugin.getLog().info("DAWN BANK PLACEHOLDER REGISTERED!");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dawn"; // This makes the prefix %dawn_bank_...%
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(",", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Keeps the expansion registered across PAPI reloads
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        //plugin.getLog().info("You're running me?");
        if(player == null) return null;

        final DawnPlayer dp = dawnRegistry.getDawnPlayer(player.getUniqueId());

        if(dp == null || dp.getBank() == null) return null;

        return switch (params.toLowerCase()) {
            case "bank_balance" -> formatter.format(dp.getBank().getBalance());
            case "bank_balance_raw" -> String.valueOf(dp.getBank().getBalance());
            case "bank_tier" -> dp.getBank().getTier();
            default -> null;
        };
    }
}
