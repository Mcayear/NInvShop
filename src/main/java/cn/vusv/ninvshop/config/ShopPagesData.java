package cn.vusv.ninvshop.config;

import cn.nukkit.utils.Config;
import cn.vusv.ninvshop.NInvShop;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopPagesData {
    public static LinkedHashMap<String, ShopPagesData> ShopPagesMap;

    public static void init() {
        ShopPagesMap = new LinkedHashMap<>();
        File[] files = new File(NInvShop.getInstance().getDataFolder() + "/ShopPages").listFiles();

        if (files == null) {
            NInvShop.getInstance().getLogger().error("ShopPages 文件夹不存在。");
            return;
        }
        for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().replace(".yml", "");
            try {
                ShopPagesMap.put(fileName, new ShopPagesData(fileName, new Config(file, Config.YAML)));
            } catch (java.lang.ClassCastException err) {
                NInvShop.getInstance().getLogger().info("加载失败 "+fileName);
                throw err;
            }
        }
    }

    private String shopName = "";

    private String row = "";

    private String icon = "";

    private boolean isOnlyConsole = false;

    private List<ItemData> itemList;
    public ShopPagesData(String fileName, Config config) {
        this.shopName = fileName;
        this.row = config.getString("row", "");
        this.icon = config.getString("icon", "");
        this.isOnlyConsole = config.getBoolean("onlyConsole", false);
        this.itemList = (List<ShopPagesData.ItemData>) config.getList("data", ImmutableList.of())
                .stream()
                .map(item -> new ItemData((Map<String, Object>) item))
                .collect(Collectors.toList());
    }

    public String getShopName() {
        return shopName;
    }
    public String getRow() {
        return row;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isOnlyConsole() {
        return isOnlyConsole;
    }

    public List<ItemData> getItemList() {
        return itemList;
    }

    public static class ItemData {
        private String showitem;
        private ItemInfo iteminfo;
        private int price;
        private int bulkBuy;
        private String showNeed;
        private List<String> need;
        private List<String> execcmd;
        private boolean onlycmd;
        private BuyLimits buyLimits;
        private boolean direct = false;// 直接购买1个

        public ItemData(Map<String, Object> data) {
            this.showitem = (String) data.getOrDefault("showitem", "");
            if (data.containsKey("iteminfo")) {
                this.iteminfo = new ItemInfo((Map<String, Object>) data.getOrDefault("iteminfo", Map.of()));
            }
            this.price = (int) data.getOrDefault("price", 0);
            this.bulkBuy = (int) data.getOrDefault("bulk_buy", 1);
            this.showNeed = (String) data.getOrDefault("showNeed", "");
            this.need = (List<String>) data.getOrDefault("need", List.of());
            this.execcmd = (List<String>) data.getOrDefault("execcmd", List.of());
            this.onlycmd = (boolean) data.getOrDefault("onlycmd", false);
            if (data.containsKey("buyLimits")) {
                this.buyLimits = new BuyLimits((Map<String, Object>) data.getOrDefault("buyLimits", Map.of()));
            }
            this.direct = (boolean) data.getOrDefault("direct", false);
        }

        public String getShowitem() {
            return showitem;
        }

        public ItemInfo getIteminfo() {
            return iteminfo;
        }

        public int getPrice() {
            return price;
        }

        public int getBulkBuy() {
            return bulkBuy;
        }

        public String getShowNeed() {
            return showNeed;
        }

        public List<String> getNeed() {
            return need;
        }

        public List<String> getExeccmd() {
            return execcmd;
        }

        public boolean isOnlycmd() {
            return onlycmd;
        }

        public BuyLimits getBuyLimits() {
            return buyLimits;
        }

        public boolean isDirect() {
            return direct;
        }
    }

    public static class ItemInfo {
        @Getter
        private String name;
        @Getter
        private String lore;
        public ItemInfo(Map<String, Object> itemInfo) {
            this.name = (String) itemInfo.getOrDefault("name", "");
            this.lore = (String) itemInfo.getOrDefault("lore", "");
        }
    }

    public static class BuyLimits {
        private String uid;
        private int maxNum;
        private int refreshTimeDay;
        private int refreshTimeHour;

        public BuyLimits(Map<String, Object> buyLimits) {
            this.uid = (String) buyLimits.getOrDefault("uid", "");
            this.maxNum = (int) buyLimits.getOrDefault("maxNum", 0);
            this.refreshTimeDay = (int) buyLimits.getOrDefault("refreshTimeDay", 0);
            this.refreshTimeHour = (int) buyLimits.getOrDefault("refreshTimeHour", 0);
        }

        public String getUid() {
            return uid;
        }

        public int getMaxNum() {
            return maxNum;
        }

        public int getRefreshTimeDay() {
            return refreshTimeDay;
        }

        public int getRefreshTimeHour() {
            return refreshTimeHour;
        }
    }
}
