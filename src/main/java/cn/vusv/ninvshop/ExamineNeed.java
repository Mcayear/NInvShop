package cn.vusv.ninvshop;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.vusv.ninvshop.adapter.Econ;
import cn.vusv.ninvshop.adapter.PointCoupon;
import cn.vusv.ninvshop.config.McrmbConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExamineNeed {
    public static boolean examineNeed(String[] needArray, Player player) {
        List<String> itemNeedList = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        int needMoney = 0;
        int needRMB = 0;
        for (int i = 0; i < needArray.length; i++) {
            String[] type = needArray[i].split("@");
            if (type[0].equals("money")) {
                needMoney += Integer.parseInt(type[1]);
                continue;
            } else if (type[0].equals("rmb")) {
                needRMB += Integer.parseInt(type[1]);
                continue;
            }
            Item item = Utils.parseItemString(needArray[i], player.getLanguageCode());
            if (item == null) {
                NInvShop.getInstance().getLogger().warning("配置文件中需求有误: " + String.join("||", needArray));
                return false;
            }
            if (player.getInventory().contains(item)) {
                itemList.add(item);
            } else {
                itemNeedList.add((item.getCustomName() != null ? item.getCustomName() : item.getName()) + " §r*" + item.getCount());
            }
        }
        if (itemNeedList.size() > 0) {
            player.sendMessage(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.need_failed_msg", String.join("、", itemNeedList)));
            return false;
        }
        Econ EconAPI = new Econ(player);
        if (needMoney > 0) {
            if (EconAPI.getMoney() < needMoney) {
                player.sendMessage(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.need_failed_msg", "Money *" + (needMoney - EconAPI.getMoney())));
                return false;
            }
            EconAPI.reduceMoney(needMoney);
        }
        if (needRMB > 0) {
            Map<String, String> params = new HashMap<>();
            params.put("wname", player.getName().replace(" ", "_"));
            params.put("money", String.valueOf(needRMB));
            params.put("use", "VIP-Shop");
            Map<String, Object> n3 = PointCoupon.sendGet("Pay", params);

            int code = Integer.parseInt((String) n3.get("code"));
            if (code == 0) {
                return false;
            }
            if (code != 101) {
                player.sendMessage("§c点券余额不足，请充值。\n唯一充值官网：" + McrmbConfig.website);
                return false;
            }
        }
        for (Item v : itemList) {
            player.getInventory().removeItem(v);
        }
        return true;
    }

}