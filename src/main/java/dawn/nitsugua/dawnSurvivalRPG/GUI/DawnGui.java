package dawn.nitsugua.dawnSurvivalRPG.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class DawnGui {
    private final String id;

    protected DawnGui(String id) {
        this.id = id;
    }

    public String getId() {return id;}

    // Title can depend on the viewer (name, page number, etc.)
    public abstract Component title(Player viewer);

    // Number of rows, 1-6
    public abstract int rows();

    // Fill the GUI with buttons. Runs every time it opens or refreshes.
    public abstract void populate(DawnGuiHolder holder);
}
