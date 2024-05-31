package cn.vusv.ninvshop;

import RcRPG.RcRPGMain;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Ornament;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import RcTaskBook.books.Book;
import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.ItemBean;
import cn.ankele.plugin.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.lang.LangCode;
import cn.nukkit.utils.ConfigSection;
import cn.vusv.ninvshop.config.PlayerBuyData;
import cn.vusv.ninvshop.config.ShopPagesData;

import java.time.Instant;
import java.util.LinkedHashMap;

import static cn.ankele.plugin.utils.BaseCommand.createItem;
import static java.lang.Integer.parseInt;

public class Utils {
    public static long getNowTime() {
        Instant timestamp = Instant.now();
        return timestamp.toEpochMilli();
    }

    public static Item inBackpack(PlayerInventory playerInv, String str, LangCode langCode) {
        Item parseItem = parseItemString(str, langCode);
        if (parseItem.isNull()) return parseItem;
        if (!(str.startsWith("rcrpg@") || str.startsWith("nweapon@"))) return parseItem;
        String[] args = str.split("@")[1].split(" ");
        for (int i = 0; i < playerInv.getSize(); ++i) {
            Item item = playerInv.getItem(i);
            if (item.getId() == Item.AIR || item.getCount() <= 0) continue;
            if (item.getNamedTag() == null) continue;
            if (!item.getNamedTag().contains("type")) continue;
            if (!item.getNamedTag().contains("name")) continue;
            if (item.getNamedTag().getString("type").equals(args[0])
                    && item.getNamedTag().getString("name").equals(args[1])) {
                return item;
            }

        }
        return parseItem;
    }

    public static Item parseItemString(String str, LangCode langCode) {
        String[] arr = str.split("@");
        if (arr[0].equals("mi")) {// mi@1 代金券
            if (Server.getInstance().getPluginManager().getPlugin("MagicItem") == null) {
                NInvShop.getInstance().getLogger().warning("你没有使用 MagicItem 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
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
                NInvShop.getInstance().getLogger().warning("MagicItem物品不存在：" + args[1]);
            }
        } else if (arr[0].equals("item")) {
            String[] args = arr[1].split(" ");
            Item item = Item.fromString(args[0]);
            if (args.length == 2) {
                item.setCount(parseInt(args[1]));
            } else {
                item.setDamage(parseInt(args[1]));
                item.setCount(parseInt(args[2]));
            }
            return item;
        } else if (arr[0].equals("nweapon") || arr[0].equals("rcrpg")) {
            if (Server.getInstance().getPluginManager().getPlugin("RcRPG") == null) {
                NInvShop.getInstance().getLogger().warning("你没有使用 RcRPG 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
            }
            String[] args = arr[1].split(" ");//Main.loadWeapon
            String type = args[0];
            String itemName = args[1];
            int count = 1;

            if (args.length > 2) {
                count = parseInt(args[2]);
            }

            switch (type) {
                case "护甲", "防具", "armour", "armor" -> {
                    if (RcRPGMain.loadArmour.containsKey(itemName)) {
                        return Armour.getItem(itemName, count);
                    }
                }
                case "武器", "weapon" -> {
                    if (RcRPGMain.loadWeapon.containsKey(itemName)) {
                        return Weapon.getItem(itemName, count);
                    }
                }
                case "宝石", "stone", "gem" -> {
                    if (RcRPGMain.loadStone.containsKey(itemName)) {
                        return Stone.getItem(itemName, count);
                    }
                }
                case "饰品", "ornament", "jewelry" -> {
                    if (RcRPGMain.loadOrnament.containsKey(itemName)) {
                        return Ornament.getItem(itemName, count);
                    }
                }
                case "锻造图" -> {
                }
                case "宝石券", "精工石", "强化石", "锻造石" -> {
                }
            }
            return Item.AIR_ITEM;
            //return nWeapon.onlyNameGetItem(args[0], args[1], args[2], null);
        } else if (arr[0].equals("taskbook")) {
            if (Server.getInstance().getPluginManager().getPlugin("RcTaskBook") == null) {
                NInvShop.getInstance().getLogger().warning("你没有使用 RcTaskBook 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
            }
            String[] args = arr[1].split(" ");
            return Book.getBook(langCode, args[1], Integer.parseInt(args[0]));
        } else {
            NInvShop.getInstance().getLogger().warning("物品配置有误：" + str);
        }
        return Item.AIR_ITEM;
    }
    public static Item parseItemString(String str) {
        return parseItemString(str, LangCode.zh_CN);
    }

    public static int defaultVaule(int value) {
        if (value == 0) {
            return 1;
        }
        return value;
    }

    /**
     * 向玩家背包添加物品
     *
     * @param player 要添加物品的玩家
     * @param item   要添加到玩家背包的物品
     */
    public static void addItemToPlayer(Player player, Item item) {
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        } else {
            player.sendPopup(NInvShop.getI18n().tr(player.getLanguageCode(), "ninvshop.item.item_drop_tips", item.getName()));
            player.getLevel().dropItem(player, item);
        }
    }

    /**
     * 返回还可以购买的数量
     * @param player
     * @param itemData
     * @return
     */
    public static int compLimBuyCount(Player player, ShopPagesData.ItemData itemData) {
        int limBuyCount = 0;
        ConfigSection pBuyData = PlayerBuyData.getPlayerData(player.getName(), itemData.getBuyLimits().getUid());
        int limHour = defaultVaule(itemData.getBuyLimits().getRefreshTimeDay()) + defaultVaule(itemData.getBuyLimits().getRefreshTimeDay()) * 24;

        if (pBuyData.exists("buyCount")) {// 判断是否有玩家购买数据
            int canBuyCount = itemData.getBuyLimits().getMaxNum() - pBuyData.getInt("buyCount");
            if (canBuyCount < 1) {// 判断能否继续购买物品
                if (itemData.getBuyLimits().getRefreshTimeDay() == 0 && itemData.getBuyLimits().getRefreshTimeHour() == 0) {
                    return 0;
                }
                int compTime = (int) ((getNowTime() - pBuyData.getLong("buyTime")) / 36e5);
                if (compTime > limHour) {// 判断时间
                    PlayerBuyData.setPlayerData(player.getName(), itemData.getBuyLimits().getUid(), 0, 0);
                    limBuyCount = itemData.getBuyLimits().getMaxNum();
                } else {
                    limBuyCount = 0;
                }
            } else {
                limBuyCount = canBuyCount;
            }
        } else {
            limBuyCount = itemData.getBuyLimits().getMaxNum();
        }
        return limBuyCount;
    }

}
