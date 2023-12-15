package cn.vusv.ninvshop.config;

import cn.nukkit.utils.Config;
import cn.vusv.ninvshop.NInvShop;

import java.io.File;

public class McrmbConfig {
    static public String website = "";
    static public String sid = "";
    static public String key = "";
    static public void init() {
        Config cfg = new Config(NInvShop.INSTANCE.getDataFolder()+"/mcrmbConfig.yml", Config.YAML);
        website = cfg.getString("website", "");
        sid = cfg.getString("sid", "");
        key = cfg.getString("key", "");
    }

}
