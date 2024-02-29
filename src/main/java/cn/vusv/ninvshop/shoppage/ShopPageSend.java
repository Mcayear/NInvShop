package cn.vusv.ninvshop.shoppage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import cn.vusv.ninvshop.NInvShop;
import cn.vusv.ninvshop.config.ShopPagesData;
import cn.vusv.ninvshop.window.sendBuyWin;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.ArrayList;
import java.util.List;

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
        //HashMap<Item, Integer> itemSlots = new HashMap<>();

        FakeInventory inv;
        if (itemList.size() > 25) {
            inv = new FakeInventory(InventoryType.DOUBLE_CHEST);
        } else {
            inv = new FakeInventory(InventoryType.CHEST);
        }

        for (int i = 0; i < inv.getSize() && i < itemList.size(); i++) {
            ShopPagesData.ItemData itemData = itemList.get(i);

            if (itemData.getShowitem().isEmpty()) {
                NInvShop.getInstance().getLogger().info(shopPage.getShopName() + "页面的 showItem 为空");// TODO: test
            }
            Item item = parseItemString(itemData.getShowitem(), player.getLanguageCode());
            if (item == null) {
                item = Item.fromString("minecraft:stone");
            }
            List<String> loreList = new ArrayList<>();
            for (String str : item.getLore()) {
                loreList.add(str);
            }

            loreList.add("");
            if (itemData.getPrice() == 0) {
                loreList.add(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.shop_need"));
                loreList.add(itemData.getShowNeed());
            } else {
                loreList.add(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.shop_price") + itemData.getPrice());
            }

            if (itemData.getBuyLimits() != null) {
                int limBuyCount = compLimBuyCount(player, itemData);
                if (limBuyCount == 0) {
                    loreList.add(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.shop_sold"));
                } else {
                    loreList.add(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.shop_remaining_quantity") + limBuyCount);
                }
            }

            item.setLore(loreList.toArray(new String[0]));
            inv.setItem(i, item);
            //itemSlots.put(item, i);
        }

        String name = NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.shop_page_title", pageName);
        if (!name.isEmpty()) {
            inv.setTitle(name);
        }

        final boolean[] isClosed = {false};
        inv.setDefaultItemHandler((item, event) -> {
            int slot = -1; // 初始化为无效值，以防找不到合适的槽位

            for (InventoryAction action : event.getTransaction().getActions()) {
                if (action instanceof SlotChangeAction slotChange) {
                    if (slotChange.getInventory() instanceof FakeInventory) {
                        slot = slotChange.getSlot();
                        break;
                    }
                }
            }

            event.setCancelled(true);
            if (item.isNull()) return;

            if (!isClosed[0]) {
                inv.close(player);
                isClosed[0] = true;
            }
            int finalSlot = slot;
            Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    if (finalSlot >= 0 && finalSlot < itemList.size()) {
                        new sendBuyWin(player, shopPage, itemList.get(finalSlot), item);
                    } else {
                        // 处理索引超出范围的情况，可以抛出异常或者记录错误信息
                        // 例如：throw new IndexOutOfBoundsException("Invalid slot index: " + slot);
                        // 或者：logger.error("Invalid slot index: {}", slot);
                    }
                }
            }, 10);
        });
        player.addWindow(inv);
    }
}
