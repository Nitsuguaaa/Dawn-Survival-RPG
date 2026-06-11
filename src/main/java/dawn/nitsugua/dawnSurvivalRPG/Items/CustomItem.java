package dawn.nitsugua.dawnSurvivalRPG.Items;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomItem {
    //this is the custom item object.
    protected String customMaterial;
    protected String name;
    protected Material parentMaterial;
    protected String customModelData; //why protected? Because CustomWeapon or other classes might extend

    public CustomItem(@NotNull String customMaterial, @NotNull String name, @NotNull Material parentMaterial, @Nullable String customModelData) {
        this.customMaterial = customMaterial;
        this.name = name;
        this.parentMaterial = parentMaterial;
        this.customModelData = (customModelData != null) ? customModelData : "none";
    }

    //should onUse be here? Anyway I'll still add it. Turns out I don't need it here
    public void onUse() {
        //check if the item has an attached class with the ability...hmm...
    }

    public String getCustomMaterial() { return customMaterial; }
    public void setCustomMaterial(String customMaterial) {
        this.customMaterial = customMaterial;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public Material getParentMaterial() { return parentMaterial; }
    public void setParentMaterial(Material parentMaterial) {
        this.parentMaterial = parentMaterial;
    }

    public String getCustomModelData() { return (customModelData.equals("none")) ? customModelData : null; }
    public void setCustomModelData(String customModelData) {
        //check here if there's the same custom model data
        this.customModelData = customModelData;
    }
}
