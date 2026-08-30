package dawn.nitsugua.dawnSurvivalRPG.ExcellentEconomy;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

public class DawnExcellentEconomy {
    private final DawnSurvivalRPG plugin;
    private ExcellentEconomyAPI economyAPI;

    public DawnExcellentEconomy(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public boolean init() {
        if(Bukkit.getPluginManager().getPlugin("ExcellentEconomy") == null) {
            plugin.getLog().error("ExcellentEconomy not found! Economy features disabled");
            return false;
        }

        RegisteredServiceProvider<ExcellentEconomyAPI> provider = Bukkit.getServicesManager().getRegistration(ExcellentEconomyAPI.class);

        if(provider == null) {
            plugin.getLog().error("ExcellentEconomy API not registered!");
            return false;
        }

        this.economyAPI = provider.getProvider();
        plugin.getLog().info(TextUtil.sendLogInfo("DawnExcellentEconomy", TextUtil.LogInfo.SUCCESS,"ExcellentEconomy hooked successfully!"));
        return true;
    }

    public ExcellentEconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public boolean isReady() {
        return economyAPI != null;
    }
}
