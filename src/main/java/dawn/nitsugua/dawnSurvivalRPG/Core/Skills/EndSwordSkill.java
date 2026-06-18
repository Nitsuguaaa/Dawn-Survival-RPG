package dawn.nitsugua.dawnSurvivalRPG.Core.Skills;

import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EndSwordSkill implements Skill{
    @Override
    public String getId() {
        return "END_SWORD_SKILL";
    }

    @Override
    public void onUse(Player player, ItemStack item) {
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "You used the End Sword Skill!"));
    }
}
