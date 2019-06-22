package ink.ptms.cronus.database.data;

import com.ilummc.tlib.bungee.chat.ComponentSerializer;
import ink.ptms.cronus.Cronus;
import ink.ptms.cronus.CronusAPI;
import ink.ptms.cronus.event.CronusQuestSuccessEvent;
import ink.ptms.cronus.event.CronusStageAcceptEvent;
import ink.ptms.cronus.event.CronusStageSuccessEvent;
import ink.ptms.cronus.internal.Quest;
import ink.ptms.cronus.internal.QuestBook;
import ink.ptms.cronus.internal.QuestStage;
import ink.ptms.cronus.internal.program.Action;
import ink.ptms.cronus.internal.program.QuestProgram;
import me.skymc.taboolib.bookformatter.BookFormatter;
import me.skymc.taboolib.bookformatter.builder.BookBuilder;
import me.skymc.taboolib.common.serialize.TSerializable;
import me.skymc.taboolib.json.tellraw.TellrawJson;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @Author 坏黑
 * @Since 2019-05-24 13:34
 */
public class DataQuest implements TSerializable {

    // 任务数据
    private YamlConfiguration dataQuest = new YamlConfiguration();
    // 阶段数据
    private YamlConfiguration dataStage = new YamlConfiguration();
    // 任务序号
    private String currentQuest;
    // 阶段序号
    private String currentStage;
    // 开始时间
    private long timeStart;

    public DataQuest() {
    }

    public DataQuest(String currentQuest, String currentStage) {
        this.currentQuest = currentQuest;
        this.currentStage = currentStage;
        this.timeStart = System.currentTimeMillis();
    }

    /**
     * 检查玩家任务状态
     * 如果完成所有条目则前往下个阶段
     * 如果是最终阶段则创建任务完成时间变量
     */
    public void checkAndComplete(Player player) {
        DataPlayer playerData = CronusAPI.getData(player);
        Quest quest = getQuest();
        if (quest == null) {
            return;
        }
        QuestStage stage = getStage();
        if (stage == null) {
            return;
        }
        // 阶段已完成
        if (!stage.isCompleted(this)) {
            return;
        }
        // 获取阶段
        int index = getStageIndex(quest) + 1;
        // 最终阶段
        if (index >= quest.getStage().size()) {
            // 已经完成任务
            if (playerData.isQuestCompleted(quest.getId())) {
                return;
            }
            // 执行阶段奖励
            CronusStageSuccessEvent.call(player, quest, stage);
            stage.eval(new QuestProgram(player, this), Action.SUCCESS);
            // 执行任务奖励
            CronusQuestSuccessEvent.call(player, quest);
            quest.eval(new QuestProgram(player, this), Action.SUCCESS);
            // 更新任务状态
            playerData.setQuestCompleted(quest.getId(), true);
        } else {
            QuestStage stageNext = quest.getStage().get(index);
            // 执行阶段奖励
            CronusStageSuccessEvent.call(player, quest, stage);
            stage.eval(new QuestProgram(player, this), Action.SUCCESS);
            // 执行阶段奖励
            CronusStageAcceptEvent.call(player, quest, stageNext);
            stageNext.eval(new QuestProgram(player, this), Action.ACCEPT);
            // 更新任务状态
            playerData.setQuestCompleted(quest.getId(), false);
            // 更新任务阶段
            currentStage = stageNext.getId();
            // 清空阶段数据
            dataStage = new YamlConfiguration();
        }
    }

    public int getStageIndex(Quest quest) {
        int index = 0;
        for (QuestStage questStage : quest.getStage()) {
            if (questStage.getId().equals(currentStage)) {
                break;
            }
            index++;
        }
        return index;
    }

    public boolean open(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(Cronus.getInst(), () -> BookFormatter.openPlayer(player, toBuilder(player).build()));
        return true;
    }

    public BookBuilder toBuilder(Player player) {
        DataPlayer playerData = CronusAPI.getData(player);
        QuestStage stage = getStage();
        BookBuilder bookBuilder = BookFormatter.writtenBook();
        for (List<String> content : playerData.isQuestCompleted(currentQuest) ? stage.getContentCompleted() : stage.getContent()) {
            TellrawJson json = TellrawJson.create();
            for (String line : content) {
                QuestBook.appendLine(json, new QuestProgram(player, this), line);
            }
            bookBuilder.addPages(ComponentSerializer.parse(json.toRawMessage(player)));
        }
        return bookBuilder;
    }

    @Nullable
    public Quest getQuest() {
        return Cronus.getCronusService().getRegisteredQuest().get(currentQuest);
    }

    @Nullable
    public QuestStage getStage() {
        Quest quest = getQuest();
        return quest == null ? null : quest.getStage().stream().filter(s -> s.getId().equals(currentStage)).findFirst().orElse(null);
    }

    @Override
    public void read(String fieldName, String value) {
        switch (fieldName) {
            case "dataStage":
                try {
                    dataStage.loadFromString(value);
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                break;
            case "dataQuest":
                try {
                    dataQuest.loadFromString(value);
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public String write(String fieldName, Object value) {
        switch (fieldName) {
            case "dataStage":
                return dataStage.saveToString();
            case "dataQuest":
                return dataQuest.saveToString();
        }
        return null;
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public YamlConfiguration getDataStage() {
        return dataStage;
    }

    public YamlConfiguration getDataQuest() {
        return dataQuest;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public String getCurrentQuest() {
        return currentQuest;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }
}