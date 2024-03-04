package cn.vusv.ninvshop.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.utils.TextFormat;
import cn.vusv.ninvshop.NInvShop;
import cn.vusv.ninvshop.adapter.CodeException;
import cn.vusv.ninvshop.config.PlayerBuyData;
import cn.vusv.ninvshop.config.ShopPagesData;
import cn.vusv.ninvshop.shoppage.ShopPageSend;
import cn.vusv.ninvshop.window.sendShopListWin;

import static cn.vusv.ninvshop.adapter.PointCoupon.checkMoney;

public class NInvShopCommand extends Command {
    protected NInvShop api;
    protected PluginI18n i18n;

    public NInvShopCommand(String name) {
        /*
        1.the name of the command must be lowercase
        2.Here the description is set in with the key in the language file,Look at en_US.lang or zh_CN.lang.
        This can send different command description to players of different language.
        You must extends PluginCommand to have this feature.
        */
        super(name, "ninvshop.shopcommand.description");

        //Set the alias for this command
        this.setAliases(new String[]{"shop"});

        this.setPermission("plugin.ninvshop");

        /*
         * The following begins to set the command parameters, first need to clean,
         * because NK will fill in several parameters by default, we do not need.
         * */
        this.getCommandParameters().clear();

        /*
         * 1.getCommandParameters return a Map<String,cn.nukkit.command.data.Com mandParameter[]>,
         * in which each entry can be regarded as a subcommand or a command pattern.
         * 2.Each subcommand cannot be repeated.
         * 3.Optional arguments must be used at the end of the subcommand or consecutively.
         */
        this.getCommandParameters().put("reload-config", new CommandParameter[]{
                CommandParameter.newEnum("reload", false, new String[]{"reload"})
        });
        this.getCommandParameters().put("check-mcrmb", new CommandParameter[]{
                CommandParameter.newEnum("checkmcrmb", false, new String[]{"checkmcrmb"}),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
        this.getCommandParameters().put("test-shop", new CommandParameter[]{
                CommandParameter.newEnum("test", false, new String[]{"test"}),
                CommandParameter.newType("pageName", false, CommandParamType.STRING)
        });
        this.getCommandParameters().put("open-shop", new CommandParameter[]{
                CommandParameter.newEnum("open", false, new String[]{"open"}),
                CommandParameter.newType("pageName", false, CommandParamType.STRING),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
        this.getCommandParameters().put("list-shop", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new String[]{"x"}),
                CommandParameter.newEnum("action", false, new String[]{"list"}),
        });
        this.getCommandParameters().put("craft-shop", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new String[]{"x"}),
                CommandParameter.newEnum("action", false, new String[]{"craft"}),
                CommandParameter.newType("pageName", false, CommandParamType.STRING),
                CommandParameter.newType("row", true, CommandParamType.STRING),
                CommandParameter.newType("icon", true, CommandParamType.STRING)
        });
        this.getCommandParameters().put("add-slot", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new String[]{"x"}),
                CommandParameter.newEnum("action", false, new String[]{"add"}),
                CommandParameter.newType("prices", false, CommandParamType.INT),
                CommandParameter.newType("pageName", false, CommandParamType.STRING),
                CommandParameter.newType("commands", true, CommandParamType.STRING),
                CommandParameter.newEnum("isOnlyCommands", true, CommandEnum.ENUM_BOOLEAN),
        });
        this.getCommandParameters().put("change-slot", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new String[]{"x"}),
                CommandParameter.newEnum("action", false, new String[]{"remove", "set"}),
                CommandParameter.newType("index", false, CommandParamType.INT),
                CommandParameter.newType("pageName", false, CommandParamType.STRING)
        });
        api = NInvShop.getInstance();
        i18n = NInvShop.getI18n();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        LangCode langCode = sender.isPlayer() ? ((Player) sender).getLanguageCode() : LangCode.zh_CN;

        if (args.length < 1) {
            sender.sendMessage("缺少参数");
            return false;
        }
        switch (args[0]) {
            case "reload" -> {
                PlayerBuyData.init();
                ShopPagesData.init();
                sender.sendMessage(TextFormat.GREEN + i18n.tr(langCode, "ninvshop.reload_success"));
                return true;
            }
            case "checkmcrmb" -> {
                int rmb = 0;
                try {
                    if (args.length == 1) {
                        rmb = checkMoney(sender.getName());
                    } else if (args.length == 2) {
                        rmb = checkMoney(args[1]);
                    }
                } catch (CodeException e) {
                    return false;
                }
                sender.sendMessage(i18n.tr(langCode, "ninvshop.checkmcrmb", rmb));
                return true;
            }
            case "test" -> {
                if (args.length < 2) {
                    sender.sendMessage("缺少参数");
                    return false;
                }
                String pageName = args[1];
                if (!ShopPagesData.ShopPagesMap.containsKey(pageName)) {
                    sender.sendMessage(TextFormat.RED + i18n.tr(langCode, "ninvshop.not_found_page", pageName));
                    return false;
                }
                ShopPagesData shopPage = ShopPagesData.ShopPagesMap.get(pageName);
                new ShopPageSend(pageName).sendPageToPlayer(shopPage, (Player) sender);
                return true;
            }
            case "open" -> {
                if (args.length < 2) {
                    sender.sendMessage("缺少参数");
                    return false;
                }
                String pageName = args[1];
                if (!ShopPagesData.ShopPagesMap.containsKey(pageName)) {
                    sender.sendMessage(TextFormat.RED + i18n.tr(langCode, "ninvshop.not_found_page", pageName));
                    return false;
                }
                ShopPagesData shopPage = ShopPagesData.ShopPagesMap.get(pageName);
                if (args.length > 2) {
                    Player player = api.getServer().getPlayer(args[2]);
                    if (!player.isValid()) {
                        sender.sendMessage("没有匹配的目标");
                        return false;
                    }
                    new ShopPageSend(pageName).sendPageToPlayer(shopPage, player);
                } else {
                    new ShopPageSend(pageName).sendPageToPlayer(shopPage, (Player) sender);
                }
                return true;
            }
            case "list" -> {
                if (!sender.isPlayer() || !sender.isOp()) {
                    sender.sendMessage(i18n.tr(langCode, "ninvshop.shopcommand.onlyplayer"));
                    return false;
                }
                new sendShopListWin((Player) sender);
            }
        }
        //A return of 0 means failure, and a return of 1 means success.
        //This value is applied to the comparator next to the commandblock.
        return false;
    }
}
