package cn.vusv.ninvshop.window;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.item.Item;
import cn.vusv.ninvshop.config.ShopPagesData;

import java.util.ArrayList;
import java.util.List;

import static cn.vusv.ninvshop.Utils.compLimBuyCount;

public class sendBuyWin implements Listener { //一般实际开发中不在这个类中写监听器
    public static final int UID = 7800101;
    private ShopPagesData.ItemData itemData;
    private Item slotItem;

    public sendBuyWin(Player player, ShopPagesData.ItemData itemData, Item slotItem) {
        this.itemData = itemData;
        this.slotItem = slotItem;
        FormWindowCustom form;
        if (itemData.getBuyLimits() == null) {
            form = buyFrom(new FormWindowCustom("Shop - 批量购买"));
        } else {
            int limBuyCount = compLimBuyCount(player, itemData);
            form = buyLimitsFrom(new FormWindowCustom("Shop - 批量购买"), limBuyCount);
        }
        player.showFormWindow(form, UID);
    }

    public FormWindowCustom buyLimitsFrom(FormWindowCustom form, int limBuyCount) {
        List<String> label = new ArrayList<>();
        if (slotItem.getCustomName().isEmpty()) {
            label.add("物品名: " + slotItem.getName()+"§r");
        } else {
            label.add("物品名: " + slotItem.getCustomName()+"§r");
        }
        if (false) label.add("每份价格: data.price");
        if (!itemData.getNeed().isEmpty()) {
            label.add("每份需求: ");
            label.add(itemData.getNeed());
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
            label.add("物品名: " + slotItem.getName()+"§r");
        } else {
            label.add("物品名: " + slotItem.getCustomName()+"§r");
        }
        if (false) label.add("每份价格: data.price");
        if (!itemData.getNeed().isEmpty()) {
            label.add("每份需求: ");
            label.add(itemData.getNeed());
            label.add("每份数量: " + slotItem.getCount());
        }
        form.addElement(new ElementLabel(String.join("\n", label)));

        // 添加一个水平滑块_1 (text, 最小值, 最大值, 滑动最小步数)
        form.addElement(new ElementSlider("购买份数", 0, itemData.getBulkBuy(), 1));  // 组件角标: 3
        return form;
    }

    // 监听器部分
    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        int id = event.getFormID(); //这将返回一个form的唯一标识`id`
        if (id != UID) return;
        FormResponseCustom response = (FormResponseCustom) event.getResponse();
        float slider = response.getSliderResponse(1);
        if (slider < 1) {
            player.sendMessage("购买数量为: "+slider);
        }
    }
}