package dawn.nitsugua.dawnSurvivalRPG.Core.Tasks;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.ManaManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;

public class ManaTasks {
    private static DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public static void initManaTasks(DawnSurvivalRPG plugin) {
        //task 1
        //do stuff here
        ScheduledTask manaRegeneration = new ScheduledTask("MANA_REGEN_TASK", ManaManager::regenMana)
                .setInterval(1).setTimeUnit(TimeUnit.TICK);
        dawnRegistry.registerTask(manaRegeneration.getTaskName(), manaRegeneration);

        //task 2
        //something here
    }
}
