package cn.vusv.ninvshop;

import cn.nukkit.Server;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.vusv.ninvshop.command.NInvShopCommand;
import cn.vusv.ninvshop.config.PlayerBuyData;
import cn.vusv.ninvshop.config.ShopPagesData;

import java.io.File;

/**
 * author: MagicDroidX
 * NukkitExamplePlugin Project
 */
public class NInvShop extends PluginBase {
    public static NInvShop INSTANCE;
    public static PluginI18n I18N;

    @Override
    public void onLoad() {
        //save Plugin Instance
        INSTANCE = this;
        //register the plugin i18n
        I18N = PluginI18nManager.register(this);
        //register the command of plugin
        this.getServer().getCommandMap().register("NInvShop", new NInvShopCommand());

        this.getLogger().info(TextFormat.WHITE + "I've been loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.DARK_GREEN + "I've been enabled!");

        //Use the plugin's i18n output
        this.getLogger().info(I18N.tr(Server.getInstance().getLanguageCode(), "exampleplugin.helloworld", "世界"));

        //Save resources
        init();
    }

    public void init() {
        this.saveResource("config.yml");
        loadPlayerBuyData();
        loadShopPages();
    }

    private void loadPlayerBuyData() {
        if (!new File(this.getDataFolder() + "/PlayerBuyData").exists()) {
            this.getLogger().info("未检测到 PlayerBuyData 文件夹，正在创建");
            if (!new File(this.getDataFolder() + "/PlayerBuyData").mkdirs()) {
                this.getLogger().error("PlayerBuyData 文件夹创建失败");
                return;
            } else {
                this.getLogger().info("PlayerBuyData 文件夹创建完成，正在载入数据");
            }
        }
        PlayerBuyData.init();
    }

    private void loadShopPages() {
        if (!new File(this.getDataFolder() + "/ShopPages").exists()) {
            this.getLogger().info("未检测到 ShopPages 文件夹，正在创建");
            if (!new File(this.getDataFolder() + "/ShopPages").mkdirs()) {
                this.getLogger().error("ShopPages 文件夹创建失败");
                return;
            } else {
                this.getLogger().info("ShopPages 文件夹创建完成，正在载入数据");
            }
        }
        ShopPagesData.init();
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.DARK_RED + "I've been disabled!");
    }
}
