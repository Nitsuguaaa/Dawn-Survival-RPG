package dawn.nitsugua.dawnSurvivalRPG.Core.Tasks;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;

public class BankAutoSaveTask {
    private static final DawnRegistry dr = DawnRegistry.getInstance();

    public static void runBankAutoSave(DawnSurvivalRPG plugin) {
        ScheduledTask runBankSaving = new ScheduledTask("BANK_AUTO_SAVE_TASK", () -> {
            int savedCount = 0;
            for (DawnPlayer dp : dr.getAllPlayers().values()) {
                if(dp.getBank() != null && dp.getBank().isDirty()) {
                    plugin.getBankDAO().saveBankAsync(dp.getBank());
                    dp.getBank().setClean();
                    savedCount++;
                }
            }

            if (savedCount > 0) {
                plugin.getLog().info(TextUtil.sendLogInfo("BankAutoSaveTask", TextUtil.LogInfo.SUCCESS, "Saved " + savedCount + " banks records to database."));
            }
        })
                .setInterval(5)
                .setTimeUnit(TimeUnit.SECOND);

        plugin.getTaskManager().registerTask(runBankSaving);
    }
}
