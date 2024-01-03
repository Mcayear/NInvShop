package cn.vusv.ninvshop;

import RcRPG.Main;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Ornament;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.ItemBean;
import cn.ankele.plugin.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.utils.ConfigSection;
import cn.vusv.ninvshop.config.PlayerBuyData;
import cn.vusv.ninvshop.config.ShopPagesData;

import java.time.Instant;
import java.util.LinkedHashMap;

import static cn.ankele.plugin.utils.Commands.createItem;
import static cn.vusv.ninvshop.NInvShop.I18N;
import static java.lang.Integer.parseInt;

public class Utils {
    public static long getNowTime() {
        Instant timestamp = Instant.now();
        long millis = timestamp.toEpochMilli();
        return millis;
    }

    public static Item parseItemString(String str) {
        String[] arr = str.split("@");
        if (arr[0].equals("mi")) {// mi@1 代金券
            if (Server.getInstance().getPluginManager().getPlugin("MagicItem") == null) {
                NInvShop.INSTANCE.getLogger().warning("你没有使用 MagicItem 插件却在试图获取它的物品：" + str);
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
                NInvShop.INSTANCE.getLogger().warning("MagicItem物品不存在：" + args[1]);
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
                NInvShop.INSTANCE.getLogger().warning("你没有使用 RcRPG 插件却在试图获取它的物品：" + str);
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
                case "护甲":
                case "防具":
                case "armour":
                case "armor": {
                    if (Main.loadArmour.containsKey(itemName)) {
                        return Armour.getItem(itemName, count);
                    }
                    break;
                }
                case "武器":
                case "weapon": {
                    if (Main.loadWeapon.containsKey(itemName)) {
                        return Weapon.getItem(itemName, count);
                    }
                    break;
                }
                case "宝石":
                case "stone":
                case "gem": {
                    if (Main.loadStone.containsKey(itemName)) {
                        return Stone.getItem(itemName, count);
                    }
                    break;
                }
                case "饰品":
                case "ornament":
                case "jewelry": {
                    if (Main.loadOrnament.containsKey(itemName)) {
                        return Ornament.getItem(itemName, count);
                    }
                    break;
                }
                case "锻造图": {
                    break;
                }
                case "宝石券":
                case "精工石":
                case "强化石":
                case "锻造石": {
                    break;
                }
            }
            return Item.AIR_ITEM;
            //return nWeapon.onlyNameGetItem(args[0], args[1], args[2], null);
        } else {
            NInvShop.INSTANCE.getLogger().warning("物品配置有误：" + str);
        }
        return Item.AIR_ITEM;
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
            player.sendPopup(I18N.tr(player.getLanguageCode(), "ninvshop.item.item_drop_tips", item.getName()));
            player.getLevel().dropItem(player, item);
        }
    }

    public static int compLimBuyCount(Player player, ShopPagesData.ItemData itemData) {
        int limBuyCount = 0;
        ConfigSection pBuyData = PlayerBuyData.getPlayerData(player.getName(), itemData.getBuyLimits().getUid());
        int limHour = defaultVaule(itemData.getBuyLimits().getRefreshTimeDay()) + defaultVaule(itemData.getBuyLimits().getRefreshTimeDay()) * 24;
        if (pBuyData.exists("buyCount")) {// 判断是否有玩家购买数据
            int canBuyCount = itemData.getBuyLimits().getMaxNum() - pBuyData.getInt("buyCount");
            if (canBuyCount < 1) {// 判断能否继续购买物品
                if ((int) ((getNowTime() - pBuyData.getLong("buyTime")) / 36e5) > limHour) {// 判断时间
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
