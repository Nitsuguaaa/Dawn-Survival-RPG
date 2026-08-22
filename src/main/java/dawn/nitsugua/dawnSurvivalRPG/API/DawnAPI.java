package dawn.nitsugua.dawnSurvivalRPG.API;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.TaskManager;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import net.kyori.adventure.text.Component;

public interface DawnAPI {

    TaskManager getTaskManager();
}
