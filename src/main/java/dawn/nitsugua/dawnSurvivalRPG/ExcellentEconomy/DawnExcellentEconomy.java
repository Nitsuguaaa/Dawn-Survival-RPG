package dawn.nitsugua.dawnSurvivalRPG.ExcellentEconomy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

public class DawnExcellentEconomy {
    RegisteredServiceProvider<ExcellentEconomyAPI> provider = Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);

    private void init() {
       if(provider != null) {
            ExcellentEconomyAPI api = provider.getProvider();
        }
    }

    //test push

}
