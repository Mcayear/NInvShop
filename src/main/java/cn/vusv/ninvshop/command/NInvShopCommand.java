package cn.vusv.ninvshop.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.BooleanNode;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.utils.TextFormat;
import cn.vusv.ninvshop.NInvShop;
import cn.vusv.ninvshop.config.PlayerBuyData;
import cn.vusv.ninvshop.config.ShopPagesData;
import cn.vusv.ninvshop.shoppage.ShopPageSend;

import java.util.List;
import java.util.Map;

import static cn.vusv.ninvshop.NInvShop.I18N;


public class NInvShopCommand extends PluginCommand<NInvShop> {

    public NInvShopCommand() {
        /*
        1.the name of the command must be lowercase
        2.Here the description is set in with the key in the language file,Look at en_US.lang or zh_CN.lang.
        This can send different command description to players of different language.
        You must extends PluginCommand to have this feature.
        */
        super("ninvshop", "exampleplugin.examplecommand.description", NInvShop.INSTANCE);

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
                CommandParameter.newEnum("reload", false, new CommandEnum("reloadAction", "reload"))
        });
        this.getCommandParameters().put("test-shop", new CommandParameter[]{
                CommandParameter.newEnum("test", false, new CommandEnum("testAction", "test")),
                CommandParameter.newType("pageName", false, CommandParamType.STRING)
        });
        this.getCommandParameters().put("open-shop", new CommandParameter[]{
                CommandParameter.newEnum("open", false, new CommandEnum("openAction", "open")),
                CommandParameter.newType("pageName", false, CommandParamType.STRING),
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode())
        });
        this.getCommandParameters().put("list-shop", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new CommandEnum("adminOp", "x")),
                CommandParameter.newEnum("action", false, new CommandEnum("listShop", "list")),
        });
        this.getCommandParameters().put("craft-shop", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new CommandEnum("adminOp", "x")),
                CommandParameter.newEnum("action", false, new CommandEnum("craftShop", "craft")),
                CommandParameter.newType("pageName", false, CommandParamType.STRING),
                CommandParameter.newType("row", true, CommandParamType.STRING),
                CommandParameter.newType("icon", true, CommandParamType.STRING)
        });
        this.getCommandParameters().put("add-slot", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new CommandEnum("adminOp", "x")),
                CommandParameter.newEnum("action", false, new CommandEnum("addSlot", "add")),
                CommandParameter.newType("prices", false, CommandParamType.INT),
                CommandParameter.newType("pageName", false, CommandParamType.STRING),
                CommandParameter.newType("commands", true, CommandParamType.STRING),
                CommandParameter.newEnum("isOnlyCommands", true, CommandEnum.ENUM_BOOLEAN, new BooleanNode()),
        });
        this.getCommandParameters().put("change-slot", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new CommandEnum("adminOp", "x")),
                CommandParameter.newEnum("action", false, new CommandEnum("changeSlot", "remove", "set")),
                CommandParameter.newType("index", false, CommandParamType.INT),
                CommandParameter.newType("pageName", false, CommandParamType.STRING)
        });
        /*
         * You'll find two `execute()` methods,
         * where `boolean execute()` is the old NK method,
         * and if you want to use the new `int execute()`,
         * you must add `enableParamTree` at the end of the constructor.
         *
         * Note that you can only choose one of these two execute methods
         */
        this.enableParamTree();
    }

    /**
     * This method is executed only if the command syntax is correct, which means you don't need to verify the parameters yourself.
     * In addition, before executing the command, will check whether the executor has the permission for the command.
     * If these conditions are not met, an error message is automatically displayed.
     *
     * @param sender       The sender of the command
     * @param commandLabel Command label. For example, if `/test 123` is used, the value is `test`
     * @param result       The parsed matching subcommand pattern
     * @param log          The command output tool, which is used to output info, can be controlled by the world's sendCommandFeedback rule
     */
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "reload-config" -> {
                PlayerBuyData.init();
                ShopPagesData.init();
                log.addSuccess(TextFormat.GREEN + I18N.tr(Server.getInstance().getLanguageCode(), "ninvshop.reload_success")).output();
                return 1;
            }
            case "test-shop" -> {
                String pageName = list.getResult(1);
                if (!ShopPagesData.ShopPagesMap.containsKey(pageName)) {
                    log.addError(TextFormat.RED + I18N.tr(Server.getInstance().getLanguageCode(), "ninvshop.not_found_page", pageName)).output();
                    return 0;
                }
                ShopPagesData shopPage = ShopPagesData.ShopPagesMap.get(pageName);
                new ShopPageSend(pageName).sendPageToPlayer(shopPage, (Player) sender);
                return 1;
            }
            case "open-shop" -> {
                String pageName = list.getResult(1);
                if (!ShopPagesData.ShopPagesMap.containsKey(pageName)) {
                    log.addError(TextFormat.RED + I18N.tr(Server.getInstance().getLanguageCode(), "ninvshop.not_found_page", pageName)).output();
                    return 0;
                }
                ShopPagesData shopPage = ShopPagesData.ShopPagesMap.get(pageName);
                if (list.hasResult(2)) {
                    List<Player> players = list.getResult(2);
                    if (players.isEmpty()) {
                        log.addNoTargetMatch().output();
                        return 0;
                    }
                    new ShopPageSend(pageName).sendPageToPlayer(shopPage, players.get(0));
                } else {
                    new ShopPageSend(pageName).sendPageToPlayer(shopPage, (Player) sender);
                }
                return 1;
            }
        }
        //A return of 0 means failure, and a return of 1 means success.
        //This value is applied to the comparator next to the commandblock.
        return 0;
    }
}
