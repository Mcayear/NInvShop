package cn.vusv.ninvshop.config;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.vusv.ninvshop.NInvShop;

import java.io.File;
import java.util.LinkedHashMap;

public class PlayerBuyData {
    static public LinkedHashMap<String, Config> PlayerBuyMap;
    static public void init() {
        PlayerBuyMap = new LinkedHashMap<>();
        File[] files = new File(NInvShop.INSTANCE.getDataFolder() + "/PlayerBuyData").listFiles();

        for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().replace(".yml", "");
            PlayerBuyMap.put(fileName, new Config(file, Config.YAML));
        }
    }

    static public ConfigSection setPlayerData(String playerName, String shopName, int buyCount, long buyTime) {
        Config config = PlayerBuyMap.getOrDefault(playerName, new Config(
                new File(NInvShop.INSTANCE.getDataFolder() + "/PlayerBuyData", playerName + ".yml"),
                Config.YAML,
                //Default values (not necessary)
                new ConfigSection()
        ));
        config.set(shopName, new ConfigSection(new LinkedHashMap<>() {
            {
                put("buyCount", buyCount);
                put("buyTime", buyTime); //you can also put other standard objects!
            }
        }));
        config.save();
        return config.getSection(shopName);
    }

    static public ConfigSection getPlayerData(String playerName, String shopName) {
        Config config = PlayerBuyMap.getOrDefault(playerName, new Config(
                new File(NInvShop.INSTANCE.getDataFolder() + "/PlayerBuyData", playerName + ".yml"),
                Config.YAML,
                //Default values (not necessary)
                new ConfigSection()
        ));
        if (config.exists(shopName)) {
            return config.getSection(shopName);
        } else {
            return new ConfigSection(new LinkedHashMap<>() {
                {
                    put("buyCount", 0);
                    put("buyTime", 0); //you can also put other standard objects!
                }
            });
        }
    }

}

