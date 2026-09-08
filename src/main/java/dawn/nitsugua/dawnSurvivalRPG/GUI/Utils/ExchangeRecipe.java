package dawn.nitsugua.dawnSurvivalRPG.GUI.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public record ExchangeRecipe(
        String displayName,
        List<String> lore,
        String icon,
        String costItemId,
        int costAmount,
        String result,
        int resultAmount
        //Consumer<Player> action
) {}
