package dawn.nitsugua.dawnSurvivalRPG.Economy;

import java.util.UUID;

public class Bounty {
    private final int id;
    private final UUID targetUuid;
    private final UUID placerUuid;
    private final double amount;
    private final long placedAt;
    private boolean active;

    public Bounty(int id, UUID targetUuid, UUID placerUuid, double amount, long placedAt) {
        this.id = id;
        this.targetUuid = targetUuid;
        this.placerUuid = placerUuid;
        this.amount = amount;
        this.placedAt = placedAt;
        this.active = true;
    }

    public int getId() { return id; }
    public UUID getTargetUuid() { return targetUuid; }
    public UUID getPlacerUuid() { return placerUuid; }
    public double getAmount() { return amount; }
    public long getPlacedAt() { return placedAt; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
