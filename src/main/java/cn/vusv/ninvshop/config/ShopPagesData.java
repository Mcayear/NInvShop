package cn.vusv.ninvshop.config;

import cn.nukkit.utils.Config;
import cn.vusv.ninvshop.NInvShop;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopPagesData {
    static public LinkedHashMap<String, ShopPagesData> ShopPagesMap = new LinkedHashMap<>();

    static public void init() {
        File[] files = new File(NInvShop.INSTANCE.getDataFolder() + "/ShopPages").listFiles();

        for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().replace(".yml", "");
            ShopPagesMap.put(fileName, new ShopPagesData(new Config(file, Config.YAML)));
        }
    }

    private String row = "";

    private String icon = "";

    private boolean isOnlyConsole = false;

    private List<ItemData> itemList;
    public ShopPagesData(Config config) {
        this.row = config.getString("row", "");
        this.icon = config.getString("icon", "");
        this.isOnlyConsole = config.getBoolean("onlyConsole", false);
        this.itemList = (List<ShopPagesData.ItemData>) config.getList("data", ImmutableList.of())
                .stream()
                .map(item -> new ItemData((Map<String, Object>) item))
                .collect(Collectors.toList());

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
        private int bulkBuy;
        private String need;
        private String needString;
        private List<String> execcmd;
        private boolean onlycmd;
        private BuyLimits buyLimits;

        public ItemData(Map<String, Object> data) {
            this.showitem = (String) data.getOrDefault("showitem", "");
            this.bulkBuy = (int) data.getOrDefault("bulk_buy", 1);
            this.need = (String) data.getOrDefault("need", "");
            this.needString = (String) data.getOrDefault("needString", "");
            this.execcmd = (List<String>) data.getOrDefault("execcmd", List.of());
            this.onlycmd = (boolean) data.getOrDefault("onlycmd", false);
            if (data.containsKey("buyLimits")) {
                this.buyLimits = new BuyLimits((Map<String, Object>) data.getOrDefault("buyLimits", Map.of()));
            }
        }

        public String getShowitem() {
            return showitem;
        }

        public int getBulkBuy() {
            return bulkBuy;
        }

        public String getNeed() {
            return need;
        }

        public String getNeedString() {
            return needString;
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
