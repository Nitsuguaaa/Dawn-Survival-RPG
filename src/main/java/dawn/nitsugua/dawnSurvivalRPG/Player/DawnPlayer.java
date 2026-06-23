package dawn.nitsugua.dawnSurvivalRPG.Player;

import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.UUID;

public class DawnPlayer {
    private final UUID playerId;


    //Attributes
    private double maxMana = 100.0; //everyone starts at base 100 mana
    private double currentMana;
    private double hp = 20.0; //for future use
    private double defense = 0.0; //for future use
    private double attackDamage = 1.0; //for future use
    private int intelligence;

    //Armor?
    private HashMap<EquipmentSlot, String> armorSet = new HashMap<>();

    public DawnPlayer(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {return playerId;}

    public int getIntelligence() {return intelligence;}
    public void addIntelligence(int intelligence) {
        this.intelligence += intelligence;
        addMaxMana(intelligence);
    }
    public void removeIntelligence(int intelligence) {
        this.intelligence -= intelligence;
        removeMaxMana(intelligence);
    }

    public double getMaxMana() {return maxMana;}
    public void addMaxMana(double amount) { this.maxMana += amount; }
    public void removeMaxMana(double amount) { this.maxMana = Math.max(0, this.maxMana - amount); }

    public double getCurrentMana() {return currentMana;}
    public void setCurrentMana(double mana) { this.currentMana = mana; }
    public void addCurrentMana(double amount) { this.currentMana = Math.min(this.currentMana + amount, maxMana); }
    public void removeCurrentMana(double amount) { this.currentMana = Math.max(0, this.currentMana - amount); }

    public HashMap<EquipmentSlot, String> getArmorSet() {return armorSet;}
    public String getSpecificArmorSlot(EquipmentSlot equipmentSlot) {
        return armorSet.getOrDefault(equipmentSlot, "AIR");
    }
    public void updateArmorSlot(EquipmentSlot equipmentSlot, String armorID) {
        armorSet.put(equipmentSlot, armorID);
        //recalc stats
    }
}
