package ink.ptms.cronus.service.dialog;

import com.google.common.collect.Lists;
import ink.ptms.cronus.Cronus;
import ink.ptms.cronus.event.CronusDialogEvent;
import ink.ptms.cronus.event.CronusReloadEvent;
import ink.ptms.cronus.service.Service;
import ink.ptms.cronus.service.selector.EntitySelector;
import ink.ptms.cronus.uranus.annotations.Auto;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.logger.TLogger;
import io.izzel.taboolib.util.Files;
import io.izzel.taboolib.util.lite.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @Author 坏黑
 * @Since 2019-06-10 18:14
 */
@Auto
public class Dialog implements Service, Listener {

    @TInject
    private static TLogger logger;
    @TInject
    private static Cooldown cooldown = new Cooldown("cronus|dialog", 200);

    private File folder;
    private List<DialogGroup> dialogs = Lists.newArrayList();

    @Override
    public void init() {
    }

    @Override
    public void cancel() {
    }

    @EventHandler
    public void e(CronusReloadEvent e) {
        long time = System.currentTimeMillis();
        folder = new File(Cronus.getInst().getDataFolder(), "dialogs");
        if (!folder.exists()) {
            Cronus.getInst().saveResource("dialogs/def.yml", true);
        }
        dialogs.clear();
        loadDialog(folder);
        logger.info(dialogs.size() + " Dialog Loaded. (" + (System.currentTimeMillis() - time + "ms)"));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void e(PlayerInteractEntityEvent e) {
        if (cooldown.isCooldown(e.getPlayer().getName(), 0)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Cronus.getInst(), () -> {
            EntitySelector selector = Cronus.getCronusService().getService(EntitySelector.class);
            for (DialogGroup dialog : dialogs) {
                if (selector.isSelect(e.getRightClicked(), dialog.getTarget())) {
                    CronusDialogEvent dialogEvent = CronusDialogEvent.call(dialog.getDialog(), e.getRightClicked(), e.getPlayer());
                    if (!dialogEvent.isCancelled()) {
                        try {
                            dialogEvent.getPack().display(e.getPlayer());
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                    return;
                }
            }
        });
    }

    public void loadDialog(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(this::loadDialog);
        } else {
            YamlConfiguration yaml = Files.loadYaml(file);
            for (String id : yaml.getKeys(false)) {
                ConfigurationSection config = yaml.getConfigurationSection(id);
                try {
                    if (config.getKeys(false).isEmpty()) {
                        logger.error("Dialog " + id + " is empty.");
                    } else {
                        dialogs.add(new DialogGroup(config));
                    }
                } catch (Throwable t) {
                    logger.error("Dialog " + id + " failed to load.");
                    t.printStackTrace();
                }
            }
        }
    }

    public DialogGroup getDialog(String id) {
        return dialogs.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
    }

    public List<DialogGroup> getDialogs() {
        return dialogs;
    }

    public File getFolder() {
        return folder;
    }
}
