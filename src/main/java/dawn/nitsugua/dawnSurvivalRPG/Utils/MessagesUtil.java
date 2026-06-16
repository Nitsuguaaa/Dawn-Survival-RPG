package dawn.nitsugua.dawnSurvivalRPG.Utils;

import net.kyori.adventure.text.Component;

public class MessagesUtil {

    public enum commonMessages {
        SUCCESS("<dark_gray>[</dark_gray><green>✔</green><dark_gray>]</dark_gray><green> "),
        FAILURE("<dark_gray>[</dark_gray><red>✘</red><dark_gray>]</dark_gray><red> "),
        MESSAGE("<dark_gray>[</dark_gray><yellow>✉</yellow><dark_gray>]</dark_gray><yellow> ");

        private final String prefix;

        commonMessages(String prefix) {
            this.prefix = prefix;
        }
    }

    public static Component sendInfo(commonMessages messageType, String body) {
        return TextUtil.format(messageType.prefix + body);
    }

    public static Component sendGenericError() {
        return TextUtil.format(commonMessages.FAILURE + "Something went wrong!");
    }

}
