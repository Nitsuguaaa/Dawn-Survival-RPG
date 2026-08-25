package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
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
    private final String TEST_NPC = "test-npc";

    public FancyNPCEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        dawnNPCList.add("test");
    }

    @EventHandler
    public void onNPCInteract(NpcInteractEvent e) {
        Player player = e.getPlayer();
        Npc npc = e.getNpc();

        if(!npc.getData().getName().equals(TEST_NPC)) return;

        e.setCancelled(true);

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Why hello there traveller!"));
    }
}
