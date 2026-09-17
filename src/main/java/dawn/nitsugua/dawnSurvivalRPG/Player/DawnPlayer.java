package dawn.nitsugua.dawnSurvivalRPG.Player;

import dawn.nitsugua.dawnSurvivalRPG.Economy.Bank;
import dawn.nitsugua.dawnSurvivalRPG.Economy.Bounty;
import org.bukkit.inventory.EquipmentSlot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DawnPlayer {
    private final UUID playerId;
    private UUID partyId;
    private String username;

    //database caches
    private Bank bank;
    private final List<Bounty> activeBounties = new ArrayList<>();
    private long firstJoin;
    private long lastSeen;

    private boolean thisFirstJoin = false;


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

    public UUID getUuid() { return playerId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }

    public List<Bounty> getActiveBounties() { return activeBounties; }

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

    public void flagFirstJoin() {
        thisFirstJoin = true;
    }
    public boolean isFirstJoin() {
        return thisFirstJoin;
    }

    public void setFirstJoin(long time) {
        firstJoin = time;
    }
    public long getFirstJoin() {return firstJoin;}

    public void setLastSeen(long time) {
        lastSeen = time;
    }
    public long getLastSeen() {return lastSeen;}

    public void setParty(UUID partyId) {this.partyId = partyId;}
    public void removeParty() {this.partyId = null;}
    public UUID getPartyId() {return partyId;}
    public boolean isInParty() {return partyId != null;}
}
