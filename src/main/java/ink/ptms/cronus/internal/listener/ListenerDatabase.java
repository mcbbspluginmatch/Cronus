package ink.ptms.cronus.internal.listener;

import ink.ptms.cronus.Cronus;
import ink.ptms.cronus.database.data.DataPlayer;
import ink.ptms.cronus.event.CronusDataPushEvent;
import io.izzel.taboolib.module.inject.TListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @Author 坏黑
 * @Since 2019-05-30 23:11
 */
@TListener
public class ListenerDatabase implements Listener {

    @EventHandler
    public void e(PlayerJoinEvent e) {
        Cronus.getCronusService().refreshData(e.getPlayer());
    }

    @EventHandler
    public void e(PlayerQuitEvent e) {
        DataPlayer dataPlayer = Cronus.getCronusService().getPlayerData().remove(e.getPlayer().getName());
        if (dataPlayer != null) {
            CronusDataPushEvent.call(e.getPlayer(), dataPlayer.push());
        }
    }
}
