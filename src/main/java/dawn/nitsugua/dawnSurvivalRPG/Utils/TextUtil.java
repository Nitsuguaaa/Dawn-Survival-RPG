package dawn.nitsugua.dawnSurvivalRPG.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtil {

    public enum ItemRarity {
        COMMON("<gray>"),
        UNCOMMON("<green>"),
        RARE("<blue>"),
        EPIC("<dark_purple>"),
        LEGENDARY("<yellow>"),
        MYTHICAL("<light_purple>");

        private final String rarity;

        ItemRarity(String rarity) {
            this.rarity = rarity;
        }
    }

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component format(String string) {
        return MINI_MESSAGE.deserialize(string);
    }

    public static Component setItemRarity(ItemRarity rarity, String itemName) {
        return MINI_MESSAGE.deserialize(rarity.rarity + itemName).decoration(TextDecoration.ITALIC, false);
    }
}
