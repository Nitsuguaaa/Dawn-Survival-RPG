package dawn.nitsugua.dawnSurvivalRPG.Items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomItem {
    //this is the custom item object.
    protected String customMaterial;
    protected String name;
    protected Material parentMaterial;
    protected String customModelData; //why protected? Because CustomWeapon or other classes might extend
    protected List<Component> lore;

    public CustomItem(@NotNull String customMaterial, @NotNull String name, @NotNull Material parentMaterial, @Nullable String customModelData) {
        this.customMaterial = customMaterial;
        this.name = name;
        this.parentMaterial = parentMaterial;
        this.customModelData = (customModelData != null) ? customModelData : "none";
    }

    //should onUse be here? Anyway I'll still add it. Turns out I don't need it here

    @Deprecated //Replaced with standalone Skill registry
    public void onUse() {
        //check if the item has an attached class with the ability...hmm...
    }

    public String getCustomMaterial() { return customMaterial; }
    public CustomItem setCustomMaterial(String customMaterial) {
        this.customMaterial = customMaterial;
        return this;
    }

    public String getName() { return name; }
    public CustomItem setName(String name) {
        this.name = name;
        return this;
    }

    public Material getParentMaterial() { return parentMaterial; }
    public CustomItem setParentMaterial(Material parentMaterial) {
        this.parentMaterial = parentMaterial;
        return this;
    }

    public String getCustomModelData() { return (!customModelData.equals("none")) ? customModelData : null; }
    public CustomItem setCustomModelData(String customModelData) {
        //check here if there's the same custom model data
        this.customModelData = customModelData;
        return this;
    }


}
