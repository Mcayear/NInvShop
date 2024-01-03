package cn.vusv.ninvshop.config;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.vusv.ninvshop.NInvShop;
import cn.vusv.ninvshop.Utils;

import java.io.File;
import java.util.LinkedHashMap;

public class PlayerBuyData {
    public static LinkedHashMap<String, Config> PlayerBuyMap;
    public static void init() {
        PlayerBuyMap = new LinkedHashMap<>();
        File[] files = new File(NInvShop.INSTANCE.getDataFolder() + "/PlayerBuyData").listFiles();

        for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().replace(".yml", "");
            PlayerBuyMap.put(fileName, new Config(file, Config.YAML));
        }
    }

    public static ConfigSection setPlayerData(String playerName, String limitUid, int buyCount, long buyTime) {
        Config config = PlayerBuyMap.getOrDefault(playerName, new Config(
                new File(NInvShop.INSTANCE.getDataFolder() + "/PlayerBuyData", playerName + ".yml"),
                Config.YAML,
                //Default values (not necessary)
                new ConfigSection()
        ));
        config.set(limitUid, new ConfigSection(new LinkedHashMap<>() {
            {
                put("buyCount", buyCount);
                put("buyTime", buyTime); //you can also put other standard objects!
            }
        }));
        config.save();
        return config.getSection(limitUid);
    }

    public static ConfigSection getPlayerData(String playerName, String limitUid) {
        Config config = PlayerBuyMap.getOrDefault(playerName, new Config(
                new File(NInvShop.INSTANCE.getDataFolder() + "/PlayerBuyData", playerName + ".yml"),
                Config.YAML,
                //Default values (not necessary)
                new ConfigSection()
        ));
        if (config.exists(limitUid)) {
            return config.getSection(limitUid);
        } else {
            return new ConfigSection(new LinkedHashMap<>() {
                {
                    put("buyCount", 0);
                    put("buyTime", 0); //you can also put other standard objects!
                }
            });
        }
    }

    public static ConfigSection addPlayerData(String playerName, String limitUid, int count) {
        ConfigSection cfg = getPlayerData(playerName, limitUid);
        cfg.set("buyCount", cfg.getInt("buyCount", 0) + count);
        if (cfg.getLong("buyTime", 0) > 0) {
            cfg.set("buyTime", Utils.getNowTime());
        }
        setPlayerData(playerName, limitUid, cfg.getInt("buyCount"), cfg.getLong("buyTime"));
        return cfg;
    }

}

