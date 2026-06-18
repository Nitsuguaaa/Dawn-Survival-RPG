package dawn.nitsugua.dawnSurvivalRPG.Core.Registry;

import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.Skill;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class DawnRegistry {
    private static final DawnRegistry INSTANCE = new DawnRegistry();
    private static final HashMap<String, CustomItem> customItems = new HashMap<>(); //Custom Items
    private static final HashMap<String, Skill> skills = new HashMap<>();

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



}
