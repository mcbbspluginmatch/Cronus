package ink.ptms.cronus.command.impl;

import ink.ptms.cronus.Cronus;
import ink.ptms.cronus.CronusAPI;
import ink.ptms.cronus.database.data.DataPlayer;
import ink.ptms.cronus.database.data.DataQuest;
import ink.ptms.cronus.event.CronusVisibleToggleEvent;
import ink.ptms.cronus.internal.QuestBook;
import io.izzel.taboolib.module.inject.TInject;
import org.bukkit.entity.Player;

/**
 * @Author 坏黑
 * @Since 2019-06-09 23:57
 */
public class CommandPlayer {

    @TInject
    static io.izzel.taboolib.module.command.lite.CommandBuilder open = io.izzel.taboolib.module.command.lite.CommandBuilder.create("CronusQuestOpen", null)
            .execute((sender, args) -> {
                if (sender instanceof Player && args.length > 0) {
                    DataQuest dataQuest = CronusAPI.getData((Player) sender).getQuest(args[0]);
                    if (dataQuest != null) {
                        dataQuest.open((Player) sender);
                    }
                }
            });

    @TInject
    static io.izzel.taboolib.module.command.lite.CommandBuilder book = io.izzel.taboolib.module.command.lite.CommandBuilder.create("CronusQuestBook", null)
            .execute((sender, args) -> {
                if (sender instanceof Player && args.length > 0) {
                    QuestBook questBook = Cronus.getCronusService().getRegisteredQuestBook().get(args[0]);
                    if (questBook != null) {
                        questBook.open((Player) sender);
                    }
                }
            });

    @TInject
    static io.izzel.taboolib.module.command.lite.CommandBuilder quit = io.izzel.taboolib.module.command.lite.CommandBuilder.create("CronusQuestQuit", null)
            .execute((sender, args) -> {
                if (sender instanceof Player && args.length > 0) {
                    DataPlayer dataPlayer = CronusAPI.getData((Player) sender);
                    DataQuest dataQuest = dataPlayer.getQuest(args[0]);
                    if (dataQuest != null) {
                        dataPlayer.failureQuest(dataQuest.getQuest());
                        dataPlayer.push();
                    }
                }
            });

    @TInject
    static io.izzel.taboolib.module.command.lite.CommandBuilder visible = io.izzel.taboolib.module.command.lite.CommandBuilder.create("CronusQuestVisible", null)
            .execute((sender, args) -> {
                if (sender instanceof Player && args.length > 0) {
                    DataPlayer dataPlayer = CronusAPI.getData((Player) sender);
                    if (dataPlayer.getQuestHide().contains(args[0])) {
                        dataPlayer.getQuestHide().remove(args[0]);
                    } else {
                        dataPlayer.getQuestHide().add(args[0]);
                    }
                    if (args.length > 1 && args[1].startsWith("-open:")) {
                        QuestBook questBook = Cronus.getCronusService().getRegisteredQuestBook().get(args[1].substring("-open:".length()));
                        if (questBook != null) {
                            questBook.open((Player) sender);
                        }
                    }
                    dataPlayer.push();
                    CronusVisibleToggleEvent.call((Player) sender);
                }
            });

}
