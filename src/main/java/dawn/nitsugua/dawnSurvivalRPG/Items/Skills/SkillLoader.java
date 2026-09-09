package dawn.nitsugua.dawnSurvivalRPG.Items.Skills;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.DungeonPearlSkill;
import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.EndSwordSkill;
import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.RepairSkill;
import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.Skill;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;

import java.util.ArrayList;

public class SkillLoader {
    private static final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    private static final Skill[] skillList = {
      new EndSwordSkill(), new RepairSkill(), new DungeonPearlSkill()
    };

    public static void initialize(DawnSurvivalRPG plugin) {
        plugin.getLog().info(TextUtil.sendLogInfo("SkillLoader", TextUtil.LogInfo.INFO, "Registering Skills..."));

        for(Skill skill : skillList) {
            plugin.getLog().info(TextUtil.sendLogInfo("SkillLoader", TextUtil.LogInfo.INFO, "Registering Skill: <white>" + skill.getId()));
            dawnRegistry.registerSkill(skill);
        }

        plugin.getLog().info(TextUtil.sendLogInfo("SkillLoader", TextUtil.LogInfo.SUCCESS, "Skills registered!"));
    }


}
