package cn.vusv.ninvshop.window;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.vusv.ninvshop.ExamineNeed;
import cn.vusv.ninvshop.NInvShop;
import cn.vusv.ninvshop.config.PlayerBuyData;
import cn.vusv.ninvshop.config.ShopPagesData;
import cn.vusv.ninvshop.shoppage.ShopPageSend;

import java.util.ArrayList;
import java.util.List;

import static cn.vusv.ninvshop.NInvShop.I18N;
import static cn.vusv.ninvshop.Utils.compLimBuyCount;

public class sendShopListWin implements Listener {
    public sendShopListWin(Player player) {
        FormWindowSimple form = new FormWindowSimple("NInvShop - 商店列表", "");
        for (String key : ShopPagesData.ShopPagesMap.keySet()) {
            form.addButton(new ElementButton(key));
        }
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) {
                return;
            }
            FormResponseSimple response = form.getResponse();
            String pageName = response.getClickedButton().getText();

            ShopPagesData shopPage = ShopPagesData.ShopPagesMap.get(pageName);
            new ShopPageSend(pageName).sendPageToPlayer(shopPage, player);
        }));
        player.showFormWindow(form);
    }
}