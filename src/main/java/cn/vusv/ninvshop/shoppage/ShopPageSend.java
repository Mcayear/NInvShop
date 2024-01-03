package cn.vusv.ninvshop.shoppage;

import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import cn.vusv.ninvshop.config.ShopPagesData;
import cn.vusv.ninvshop.window.sendBuyWin;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeSlotChangeEvent;

import java.util.ArrayList;
import java.util.List;

import static cn.vusv.ninvshop.NInvShop.I18N;
import static cn.vusv.ninvshop.Utils.compLimBuyCount;
import static cn.vusv.ninvshop.Utils.parseItemString;

public class ShopPageSend {
    private ShopPagesData shopPage;
    private String pageName;
    private List<ShopPagesData.ItemData> itemList;
    public ShopPageSend(String pageName) {
        this.pageName = pageName;
    }

    public void sendPageToPlayer(ShopPagesData shopPage, Player player) {
        this.shopPage = shopPage;
        this.itemList = shopPage.getItemList();

        ChestFakeInventory inv;
        if (itemList.size() > 25) {
            inv = new DoubleChestFakeInventory();
        } else {
            inv = new ChestFakeInventory();
        }

        for (int i = 0; i < inv.getSize() && i < itemList.size(); i++) {
            ShopPagesData.ItemData itemData = itemList.get(i);

            if (itemData.getShowitem().isEmpty()) {
                Main.getInstance().getLogger().info(shopPage.getShopName()+"页面的 showItem 为空");// TODO: test
            }
            Item item = parseItemString(itemData.getShowitem());
            if (item == null) {
                item = Item.fromString("minecraft:stone");
            }
            List<String> loreList = new ArrayList<>();
            for (String str : item.getLore()) {
                loreList.add(str);
            }

            loreList.add("");
            if (itemData.getPrice() == 0) {
                loreList.add("§r§f需求: ");
                loreList.add(itemData.getShowNeed());
            } else {
                loreList.add("§r§f售价: " + itemData.getPrice());
            }

            if (itemData.getBuyLimits() != null) {
                int limBuyCount = compLimBuyCount(player, itemData);
                if (limBuyCount == 0) {
                    loreList.add("§r§c§l已售馨");
                } else {
                    loreList.add("§r§f剩余数量: "+limBuyCount);
                }
            }

            item.setLore(loreList.toArray(new String[0]));
            inv.setItem(i, item);
        }

        String name = I18N.tr(Server.getInstance().getLanguageCode(), "ninvshop.shop_page_title", pageName);
        if (!name.isEmpty()) {
            inv.setName(name);
        }

        inv.addListener(this::onSlotChange);
        player.addWindow(inv);
    }

    private void onSlotChange(final FakeSlotChangeEvent event) {
        final int slot = event.getAction().getSlot();
        final Player player = event.getPlayer();
        event.setCancelled(true);
        player.removeWindow(event.getInventory());
        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                new sendBuyWin(player, shopPage, itemList.get(slot), event.getAction().getSourceItem());
            }
        }, 10);
    }
}
