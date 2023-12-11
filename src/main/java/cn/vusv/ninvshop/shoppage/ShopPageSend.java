package cn.vusv.ninvshop.shoppage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.vusv.ninvshop.config.ShopPagesData;
import cn.vusv.ninvshop.window.sendBuyWin;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeSlotChangeEvent;

import java.util.List;

import static cn.vusv.ninvshop.NInvShop.I18N;
import static cn.vusv.ninvshop.NInvShop.INSTANCE;
import static cn.vusv.ninvshop.Utils.parseItemString;

public class ShopPageSend {
    private String pageName;
    private List<ShopPagesData.ItemData> itemList;
    public ShopPageSend(String pageName) {
        this.pageName = pageName;
    }

    public void sendPageToPlayer(ShopPagesData shopPage, Player player) {
        itemList = shopPage.getItemList();

        ChestFakeInventory inv;
        if (itemList.size() > 25) {
            inv = new DoubleChestFakeInventory();
        } else {
            inv = new ChestFakeInventory();
        }

        for (int i = 0; i < inv.getSize() && i < itemList.size(); i++) {
            Item item = parseItemString(itemList.get(i).getShowitem());
            if (item == null) {
                item = Item.fromString("minecraft:stone");
            }
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
        player.removeAllWindows();
        new sendBuyWin(player, itemList.get(slot), event.getAction().getSourceItem());
    }
}
