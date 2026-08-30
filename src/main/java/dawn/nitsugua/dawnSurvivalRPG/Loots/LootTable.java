package dawn.nitsugua.dawnSurvivalRPG.Loots;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootTable {
    private final String id;
    private final List<LootTableEntry> entries;
    private int xpMin;
    private int xpMax;

    public LootTable(String id, List<LootTableEntry> entries) {
        this.id = id;
        this.entries = entries;
    }

    public LootTable setXpRates(int min, int max) {
        this.xpMin = min;
        this.xpMax = max;
        return this;
    }

    public String getId() {return id;}
    public List<LootTableEntry> getEntries() {return entries;}
    public int getXpMin() {return xpMin;}
    public int getXpMax() {return xpMax;}

    public int rollExp() {
        if(xpMax <= 0) return 0;
        return xpMin + ThreadLocalRandom.current().nextInt(xpMax - xpMin + 1);
    }
}
