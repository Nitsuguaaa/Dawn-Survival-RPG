package dawn.nitsugua.dawnSurvivalRPG.Loots;

public class LootTableEntry {
    private final String itemKey;
    private final int min;
    private final int max;
    private final double chance;

    public LootTableEntry(String itemKey, int min, int max, double chance) {
        this.itemKey = itemKey;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    public String getItemKey() {return itemKey;}
    public int getLootMin() {return min;}
    public int getLootMax() {return max;}
    public double getLootChance() {return chance;}
}
