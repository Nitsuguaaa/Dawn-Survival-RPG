package dawn.nitsugua.dawnSurvivalRPG.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtil {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component format(String string) {
        return MINI_MESSAGE.deserialize(string);
    }
}
