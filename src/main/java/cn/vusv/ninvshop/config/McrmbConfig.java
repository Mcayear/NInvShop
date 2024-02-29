package cn.vusv.ninvshop.config;

import cn.nukkit.utils.Config;
import cn.vusv.ninvshop.NInvShop;

import java.io.File;

public class McrmbConfig {
    public static String website = "";
    public static String sid = "";
    public static String key = "";
    public static void init() {
        Config cfg = new Config(NInvShop.getInstance().getDataFolder()+"/mcrmbConfig.yml", Config.YAML);
        website = cfg.getString("website", "");
        sid = cfg.getString("sid", "");
        key = cfg.getString("key", "");
    }

}
