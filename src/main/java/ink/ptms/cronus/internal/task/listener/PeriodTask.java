package ink.ptms.cronus.internal.task.listener;

import ink.ptms.cronus.CronusAPI;
import ink.ptms.cronus.internal.event.EventPeriod;
import ink.ptms.cronus.internal.task.other.TaskLocation;
import ink.ptms.cronus.internal.task.other.TaskPermission;
import me.skymc.taboolib.common.schedule.TSchedule;
import org.bukkit.Bukkit;

/**
 * @Author 坏黑
 * @Since 2019-06-14 17:48
 */
public class PeriodTask {

    static EventPeriod event = new EventPeriod();

    @TSchedule(period = 20, async = true)
    static void check() {
        Bukkit.getOnlinePlayers().forEach(player -> CronusAPI.stageHandle(player, event, TaskLocation.class, TaskPermission.class));
    }
}