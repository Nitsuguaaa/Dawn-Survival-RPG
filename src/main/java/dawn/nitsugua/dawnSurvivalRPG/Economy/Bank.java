package dawn.nitsugua.dawnSurvivalRPG.Economy;

import java.util.UUID;

public class Bank {
    private final UUID ownerUuid;
    private double balance;
    private String tier;
    private long lastInterest;
    private boolean dirty;

    public Bank(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
        this.balance = 0.0;
        this.tier = "COMMON";
        this.lastInterest = System.currentTimeMillis();
        this.dirty = false;
    }

    public Bank(UUID ownerUuid, double balance, String tier, long lastInterest) {
        this.ownerUuid = ownerUuid;
        this.balance = balance;
        this.tier = tier;
        this.lastInterest = lastInterest;
        this.dirty = false;
    }

    public UUID getOwnerUuid() { return ownerUuid; }
    public double getBalance() { return balance; }
    public String getTier() { return tier; }
    public long getLastInterest() { return lastInterest; }
    public boolean isDirty() { return dirty; }

    public void setBalance(double balance) {
        this.balance = balance;
        this.dirty = true;
    }

    public void setTier(String tier) {
        this.tier = tier;
        this.dirty = true;
    }

    public void setClean() {
        this.dirty = false;
    }


}
