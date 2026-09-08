package dawn.nitsugua.dawnSurvivalRPG.Utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

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

    public static void sendServerInfo(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.title(TextUtil.format("<yellow>Welcome to Dawn"));
        meta.author(TextUtil.format("<gray>The Server"));
        meta.addPages(TextUtil.format("""
                <dark_gray>Welcome to <b><gradient:#fbc4d6:#c1dff1>DawnMC</gradient></b><reset>
                <red>NOTE: player progress, worlds, and features are subject to change.<reset>
                <dark_gray>TIP: use /menu<reset>
                
                
                
                
                
                
                <dark_gray>Next page: features
                """),
                TextUtil.format("""
                        <dark_gray>Vanilla Changes:<reset>
                        <dark_gray>‣</dark_gray> Natural mob spawning is disabled
                        <dark_gray>‣</dark_gray> Elytras need to be repaired to be used
                        <dark_gray>‣</dark_gray> Netherite smithing is different
                        <dark_gray>‣</dark_gray> Some overworld structures are protected
                        """),
                TextUtil.format("""
                        <dark_gray>Features:<reset>
                        <dark_gray>‣</dark_gray> Custom items
                        <dark_gray>‣</dark_gray> Custom weapon skills
                        <dark_gray>‣</dark_gray> Crates
                        <dark_gray>‣</dark_gray> Dungeons
                        <dark_gray>‣</dark_gray> Land claiming
                        <dark_gray>‣</dark_gray> Economy
                        <dark_gray>‣</dark_gray> Bosses (coming soon)
                        """));

        book.setItemMeta(meta);

// Opens the book GUI instantly - virtual, not in inventory!
        player.openBook(book);
    }

}
