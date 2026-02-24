package cn.vusv.ninvshop;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.vusv.ninvshop.adapter.CodeException;
import cn.vusv.ninvshop.adapter.Econ;
import cn.vusv.ninvshop.adapter.PointCoupon;
import cn.vusv.ninvshop.config.McrmbConfig;
import net.player.api.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExamineNeed {
    public static boolean examineNeed(String[] needArray, Player player) {
        return examineNeed(needArray, player, "NInvShop 购买商品");
    }

    public static boolean examineNeed(String[] needArray, Player player, String reason) {
        return examineNeed(needArray, player, reason, false);
    }
    public static boolean examineNeed(String[] needArray, Player player, String reason, boolean onlyExamine) {
        List<String> itemNeedList = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        int needMoney = 0;
        int needRMB = 0;
        int needPoint = 0;
        for (int i = 0; i < needArray.length; i++) {
            String[] type = needArray[i].split("@");
            if (type[0].equals("money")) {
                needMoney += Integer.parseInt(type[1]);
                continue;
            } else if (type[0].equals("rmb")) {
                needRMB += Integer.parseInt(type[1]);
                continue;
            } else if (type[0].equals("point")) {
                needPoint += Integer.parseInt(type[1]);
                continue;
            }
            Item item = Utils.inBackpack(player.getInventory(), needArray[i], player.getLanguageCode());
            if (item.isNull()) {
                NInvShop.getInstance().getLogger().warning("配置文件中需求有误: " + String.join("||", needArray));
                return false;
            }
            if (player.getInventory().contains(item)) {
                itemList.add(item);
            } else {
                String itemName = (item.getCustomName() != null ? item.getCustomName() : item.getName()) + " §r*" + item.getCount();
                if (item.getId() == Item.WRITTEN_BOOK) {
                    itemName = item.getNamedTag().getString("title");
                }
                itemNeedList.add(itemName);
            }
        }
        if (!itemNeedList.isEmpty()) {
            if (!onlyExamine) player.sendMessage(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.need_failed_msg", String.join("、", itemNeedList)));
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
        if (needPoint > 0) {
            if (!Point.reducePoint(player, needPoint)) {
                player.sendMessage(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.cannot.point", McrmbConfig.website).replace("{n}", "\n"));
                return false;
            }
        } else if (needRMB > 0) {
            boolean isPay;
            try {
                isPay = PointCoupon.toPay(player.getName().replace(" ", "_"), needRMB, reason);
            } catch (CodeException e) {
                player.sendMessage("出现了未知错误："+e);
                return false;
            }

            if (!isPay) {
                player.sendMessage(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.cannot.point", McrmbConfig.website).replace("{n}", "\n"));
                return false;
            }
        }
        if (!onlyExamine) {
            for (Item v : itemList) {
                player.getInventory().removeItem(v);
            }
        }
        return true;
    }

}