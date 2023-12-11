package cn.vusv.ninvshop;

import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.ItemBean;
import cn.ankele.plugin.utils.Tools;
import cn.nukkit.Server;
import cn.nukkit.item.Item;

import java.time.Instant;
import java.util.LinkedHashMap;

import static java.lang.Integer.parseInt;
import static cn.ankele.plugin.utils.Commands.createItem;

public class Utils {
    static public long getNowTime() {
        Instant timestamp = Instant.now();
        long millis = timestamp.toEpochMilli();
        return millis;
    }

    static public Item parseItemString(String str) {
        String[] arr = str.split("@");
        if (arr[0].equals("mi")) {// mi@1 代金券
            if (Server.getInstance().getPluginManager().getPlugin("MagicItem") == null) {
                NInvShop.INSTANCE.getLogger().warning("你没有使用 MagicItem 插件却在试图获取它的物品：" + str);
                return null;
            }
            LinkedHashMap<String, ItemBean> items = MagicItem.getItemsMap();
            LinkedHashMap<String, Object> otherItems = MagicItem.getOthers();
            String[] args = arr[1].split(" ");
            if (items.containsKey(args[1])) {
                ItemBean item = items.get(args[1]);
                Item back = createItem(item);
                back.setCount(parseInt(args[0]));
                return back;
            } else if (otherItems.containsKey(args[1])) {
                String[] otherItemArr = ((String) otherItems.get(args[1])).split(":");
                Item item = Item.get(parseInt(otherItemArr[0]), parseInt(otherItemArr[1]));
                item.setCount(parseInt(args[0]));
                item.setCompoundTag(Tools.hexStringToBytes(otherItemArr[3]));
                return item;
            } else {
                NInvShop.INSTANCE.getLogger().warning("MagicItem物品不存在：" + args[1]);
            }
        } else if (arr[0].equals("item")) {
            String[] args = arr[1].split(" ");
            Item item = Item.fromString(args[0]);
            item.setDamage(parseInt(args[1]));
            item.setCount(parseInt(args[2]));
            return item;
        } else if (arr[0].equals("nweapon")) {
            if (Server.getInstance().getPluginManager().getPlugin("RcRPG") == null) {
                NInvShop.INSTANCE.getLogger().warning("你没有使用 RcRPG、NWeapon 插件却在试图获取它的物品：" + str);
                return null;
            }
            String[] args = arr[1].split(" ");
            //return nWeapon.onlyNameGetItem(args[0], args[1], args[2], null);
        } else {
            NInvShop.INSTANCE.getLogger().warning("物品配置有误：" + str);
        }
        return null;
    }

}
