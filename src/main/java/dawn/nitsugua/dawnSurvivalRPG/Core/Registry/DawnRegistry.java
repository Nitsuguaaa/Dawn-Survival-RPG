package dawn.nitsugua.dawnSurvivalRPG.Core.Registry;

import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.Skill;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class DawnRegistry {
    private static final DawnRegistry INSTANCE = new DawnRegistry();
    private static final HashMap<String, CustomItem> customItems = new HashMap<>(); //Custom Items
    private static final HashMap<String, Skill> skills = new HashMap<>();
    private static final HashMap<UUID, DawnPlayer> playerData = new HashMap<>();
    private static final HashMap<UUID, Double> manaCache = new HashMap<UUID, Double>();

    public DawnRegistry() {}
    public static DawnRegistry getInstance() {return INSTANCE;};

    public void registerItem(CustomItem customItem) {
        customItems.put(customItem.getCustomMaterial(), customItem);
    }
    @Nullable
    public CustomItem getCustomItem(String customMaterial) {
        return customItems.get(customMaterial);
    }

    public void registerSkill(Skill skill) {
        skills.put(skill.getId(), skill);
    }
    @Nullable
    public Skill getSkill(String skill) { return skills.get(skill);}

    public void registerPlayer(UUID playerId, DawnPlayer player) {
        playerData.put(playerId, player);
    }
    @Nullable
    public DawnPlayer getDawnPlayer(UUID player) { return playerData.get(player);}

    public void startRuntimeMana(DawnPlayer player) {
        manaCache.put(player.getPlayerId(), player.getMana());
    }

}
