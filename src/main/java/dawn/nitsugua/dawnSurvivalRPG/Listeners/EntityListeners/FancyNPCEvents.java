package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.GUI.DawnGuis.KeyExchangeGui;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class FancyNPCEvents implements Listener {
    private DawnSurvivalRPG plugin;
    private final List<String> dawnNPCList = new ArrayList<>();

    public static boolean isAvailable() {
        try {
            Class.forName("de.oliver.fancynpcs.api.events.NpcInteractEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public FancyNPCEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCInteract(NpcInteractEvent e) {
        Player player = e.getPlayer();
        Npc npc = e.getNpc();

        switch (npc.getData().getName()) {
            case "key-exchange":
                openKeyExchange(player, npc);
                break;
            case "test-npc":
                greg(player, npc);
                break;
            case "sell-npc":
                sellNpc(player, npc);
                break;
        }

    }

    private void greg(Player player, Npc npc) {
        player.sendMessage(TextUtil.sendLogInfo("Greg", TextUtil.LogInfo.SUCCESS,
                "Welcome to Dawn <white>" + player.getName() + "<green>!"
                ));
    }

    private void openKeyExchange(Player player, Npc npc) {
        plugin.getGuiManager().open(player, new KeyExchangeGui());
    }

    private void sellNpc(Player player, Npc npc) {
        plugin.getSellLoader().sellAllItems(player);
    }
}
