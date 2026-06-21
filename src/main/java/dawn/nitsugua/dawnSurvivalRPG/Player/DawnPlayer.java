package dawn.nitsugua.dawnSurvivalRPG.Player;

import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.UUID;

public class DawnPlayer {
    private final UUID playerId;


    //Attributes
    private double mana; //everyone starts at base 100 mana
    private double currentMana;
    private double hp; //for future use
    private double defense; //for future use
    private double attackDamage; //for future use

    //Armor?
    private HashMap<EquipmentSlot, String> armorSet = new HashMap<>();

    public DawnPlayer(UUID playerId, double mana) {
        this.playerId = playerId;
        this.mana = mana;
    }

    public UUID getPlayerId() {return playerId;}

    public double getMana() {return mana;}
    public void setMana(double mana) { this.mana = mana; }
    public void addMana(double amount) { this.mana += amount; }
    public void removeMana(double amount) { this.mana -= amount; }

    public double getCurrentMana() {return currentMana;}
    public void setCurrentMana(double mana) { this.currentMana = mana; }
    public void addCurrentMana(double amount) { this.currentMana += amount; }
    public void removeCurrentMana(double amount) { this.currentMana -= amount; }

    public HashMap<EquipmentSlot, String> getArmorSet() {return armorSet;}
    public String getSpecificArmorSlot(EquipmentSlot equipmentSlot) {
        return armorSet.getOrDefault(equipmentSlot, "AIR");
    }
    public void updateArmorSlot(EquipmentSlot equipmentSlot, String armorID) {
        armorSet.put(equipmentSlot, armorID);
        //recalc stats
    }
}
