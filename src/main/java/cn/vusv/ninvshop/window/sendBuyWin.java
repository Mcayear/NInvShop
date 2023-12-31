package cn.vusv.ninvshop.window;

import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.item.Item;
import cn.vusv.ninvshop.ExamineNeed;
import cn.vusv.ninvshop.NInvShop;
import cn.vusv.ninvshop.Utils;
import cn.vusv.ninvshop.adapter.Econ;
import cn.vusv.ninvshop.config.PlayerBuyData;
import cn.vusv.ninvshop.config.ShopPagesData;
import cn.vusv.ninvshop.shoppage.ShopPageSend;

import java.util.ArrayList;
import java.util.List;

import static cn.vusv.ninvshop.NInvShop.I18N;
import static cn.vusv.ninvshop.Utils.compLimBuyCount;
import static cn.vusv.ninvshop.Utils.parseItemString;

public class sendBuyWin implements Listener { //一般实际开发中不在这个类中写监听器
    private ShopPagesData shopPage;
    private ShopPagesData.ItemData itemData;
    private Item slotItem;

    public sendBuyWin(Player player, ShopPagesData shopPage_, ShopPagesData.ItemData itemData, Item slotItem) {
        this.shopPage = shopPage_;
        this.itemData = itemData;
        this.slotItem = slotItem;
        FormWindowCustom form;
        if (itemData.getBuyLimits() == null) {
            form = buyFrom(new FormWindowCustom("Shop - 批量购买"));
        } else {
            int limBuyCount = compLimBuyCount(player, itemData);
            form = buyLimitsFrom(new FormWindowCustom("Shop - 批量购买"), limBuyCount);
        }
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) {
                return;
            }
            FormResponseCustom response = form.getResponse();
            int slider = (int) response.getSliderResponse(1);
            if (slider < 1) {
                new ShopPageSend(shopPage.getShopName()).sendPageToPlayer(shopPage, player);
                return;
            }
            player.sendMessage("购买数量为: " + slider);
            slider = 1;// TODO: 实现 %total% 变量
            if (!itemData.getNeed().isEmpty()) {
                if (!ExamineNeed.examineNeed(itemData.getNeed().split("\\|\\|"), player)) {
                    player.sendMessage(I18N.tr(player.getLanguageCode(), "ninvshop.item.purchase_failed", shopPage.getShopName()));
                    return;
                }
                // 需求满足
                player.sendMessage(I18N.tr(player.getLanguageCode(), "ninvshop.item.purchase_success", shopPage.getShopName()));
                if (itemData.getBuyLimits() != null) {
                    PlayerBuyData.addPlayerData(player.getName(), itemData.getBuyLimits().getUid(), slider);
                }
            } else {// 不走 need 通道
                Econ pEcon = new Econ(player);
                if (itemData.getPrice() > 0 && itemData.getPrice() > pEcon.getMoney()) {
                    player.sendMessage(I18N.tr(player.getLanguageCode(), "ninvshop.item.not_enough_money", String.valueOf(itemData.getPrice() - pEcon.getMoney())));
                }
            }
            if (!itemData.isOnlycmd()) {
                Item item = parseItemString(itemData.getShowitem());
                if (item.isNull()) {
                    player.sendMessage("§c物品 "+itemData.getShowitem()+" 不存在");
                }
                Utils.addItemToPlayer(player, item);
            }
            for (String value : itemData.getExeccmd()) {
                String[] arr = value.split("@@");
                String cmd = arr[0].replace("%player%", player.getName()).replace("%total%", String.valueOf(slider));
                CommandSender execer;
                if (arr[1].equals("player")) {
                    execer = player;
                } else {
                    execer = Server.getInstance().getConsoleSender();
                }
                Server.getInstance().executeCommand(execer, cmd);
            }
        }));
        player.showFormWindow(form);
    }

    public FormWindowCustom buyLimitsFrom(FormWindowCustom form, int limBuyCount) {
        List<String> label = new ArrayList<>();
        if (slotItem.getCustomName().isEmpty()) {
            label.add("物品名: " + slotItem.getName() + "§r");
        } else {
            label.add("物品名: " + slotItem.getCustomName() + "§r");
        }
        if (false) label.add("每份价格: "+itemData.getPrice());
        if (!itemData.getShowNeed().isEmpty()) {
            label.add("每份需求: ");
            label.add(itemData.getShowNeed());
            label.add("每份数量: " + slotItem.getCount());
        }
        if (limBuyCount == 0) {
            label.add("");
            label.add("§r§c§l该商品已售馨");
        }
        form.addElement(new ElementLabel(String.join("\n", label)));

        // 添加一个水平滑块_1 (text, 最小值, 最大值, 滑动最小步数)
        form.addElement(new ElementSlider("购买份数", 0, limBuyCount, limBuyCount > 0 ? 1 : 0, limBuyCount > 0 ? 1 : 0));  // 组件角标: 3
        return form;
    }

    public FormWindowCustom buyFrom(FormWindowCustom form) {
        List<String> label = new ArrayList<>();
        if (slotItem.getCustomName().isEmpty()) {
            label.add("物品名: " + slotItem.getName() + "§r");
        } else {
            label.add("物品名: " + slotItem.getCustomName() + "§r");
        }
        if (false) label.add("每份价格: data.price");
        if (!itemData.getShowNeed().isEmpty()) {
            label.add("每份需求: ");
            label.add(itemData.getShowNeed());
            label.add("每份数量: " + slotItem.getCount());
        }
        form.addElement(new ElementLabel(String.join("\n", label)));

        // 添加一个水平滑块_1 (text, 最小值, 最大值, 滑动最小步数)
        form.addElement(new ElementSlider("购买份数", 0, itemData.getBulkBuy(), 1));  // 组件角标: 3
        return form;
    }
}