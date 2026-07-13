package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomItem {
    //this is the custom item object.
    private String customMaterial;
    private TextUtil.ItemRarity rarity;
    private String name;
    private Material parentMaterial;
    private String customModelData; //why private? Because CustomWeapon or other classes might extend
    private List<Component> lore;
    private String category;
    private String subCategory;

    private boolean interactable = false;
    private String interactableType; //for refactor if I actually find a place to store skill type enums

    private int intelligence;
    private Double manaCost;
    private Double cooldown;

    //variables added if custom item has an RNG
    private boolean rollable = false;
    private double dropChance;
    private int maxDropCount;

    //Decorative
    private boolean glinting = false;

    public CustomItem(@NotNull String customMaterial, @NotNull TextUtil.ItemRarity rarity, @NotNull String name, @NotNull Material parentMaterial, @NotNull String category, @NotNull String subCategory, @Nullable List<Component> lore, @Nullable String customModelData) {
        this.customMaterial = customMaterial;
        this.rarity = rarity;
        this.name = name;
        this.parentMaterial = parentMaterial;
        this.customModelData = (customModelData != null) ? customModelData : "none";
        this.lore = lore;
        this.category = category;
        this.subCategory = subCategory;
    }

    //should onUse be here? Anyway I'll still add it. Turns out I don't need it here

    /**
     * Deprecated
     * Replaced with standalone Skill registry
     */
    @Deprecated
    public void onUse() {
        //check if the item has an attached class with the ability...hmm...
    }

    public void setInteractable(Boolean bool, String interactableType, @Nullable Double manaCost, @Nullable Double cooldown) {
        this.interactable = bool;
        this.interactableType = interactableType;

        this.manaCost = manaCost;
        this.cooldown = cooldown;
    }

    public void setAttributes(@Nullable Integer intelligence) { //add more later
        if(intelligence != null) {
            this.intelligence = intelligence;
        }
    }

    public void toggleGlint() {this.glinting = true;}
    public boolean isGlinting() {return glinting;}

    public void setRollable(double dropChance, int maxDropCount) {
        rollable = true;
        this.dropChance = dropChance;
        this.maxDropCount = maxDropCount;
    }
    public boolean isRollable() {
        return rollable;
    }
    public boolean updateRollable(double dropChance, int maxDropCount) {
        if(!rollable) return false;
        this.dropChance = dropChance;
        this.maxDropCount = maxDropCount;
        return true;
    }
    public double getDropChance() {return dropChance;}
    public int getMaxDropCount() {return maxDropCount;}

    public Integer getIntelligence() {return intelligence;}

    public boolean isInteractable() {return interactable;}
    public String getInteractableType() {return interactableType;}
    public Double getManaCost() {return manaCost;}
    public Double getCooldown() {return cooldown;}

    public String getCustomMaterial() { return customMaterial; }
    public CustomItem setCustomMaterial(String customMaterial) {
        this.customMaterial = customMaterial;
        return this;
    }

    public String getItemName() { return name; }
    public CustomItem setItemName(String name) {
        this.name = name;
        return this;
    }

    public Material getParentMaterial() { return parentMaterial; }
    public CustomItem setParentMaterial(Material parentMaterial) {
        this.parentMaterial = parentMaterial;
        return this;
    }

    public String getCustomModelData() { return (customModelData.equals("none")) ? null : customModelData; }
    public CustomItem setCustomModelData(String customModelData) {
        //check here if there's the same custom model data
        this.customModelData = customModelData;
        return this;
    }

    public List<Component> getLore() {return lore.isEmpty() ? null : Collections.unmodifiableList(lore);}
    public CustomItem setLore(List<Component> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public TextUtil.ItemRarity getRarity() {return rarity;}
    public CustomItem setRarity(TextUtil.ItemRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public String getCategory() {return category;}
    public CustomItem setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getSubCategory() {return subCategory;}
    public CustomItem setSubCategory(String subCategory) {
        this.subCategory = subCategory;
        return this;
    }
}
