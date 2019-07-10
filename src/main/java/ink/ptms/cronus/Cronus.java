package ink.ptms.cronus;

import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.logger.TLogger;
import io.izzel.taboolib.util.Strings;
import io.izzel.taboolib.util.lite.Catchers;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @Author 坏黑
 * @Since 2019-05-23 18:06
 */
public class Cronus extends CronusPlugin {

    @TInject
    private static Cronus inst;
    private static CronusLoader cronusLoader = new CronusLoader();
    private static CronusService cronusService = new CronusService();
    @TInject("config.yml")
    private static TConfig conf;
    @TInject
    private static TLogger logger;

    @Override
    public void onStarting() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inst.getResource("motd.txt"), StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            bufferedReader.lines().forEach(l -> Bukkit.getConsoleSender().sendMessage(Strings.replaceWithOrder(l, inst.getDescription().getVersion())));
        } catch (Throwable ignored) {
        }
        cronusService.init();
        cronusLoader.init();
        cronusLoader.start();
    }

    @Override
    public void onStopping() {
        cronusService.cancel();
        Catchers.getPlayerdata().clear();
    }

    public void reloadQuest() {
        cronusLoader.start();
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static Cronus getInst() {
        return inst;
    }

    public static CronusLoader getCronusLoader() {
        return cronusLoader;
    }

    public static CronusService getCronusService() {
        return cronusService;
    }

    public static TConfig getConf() {
        return conf;
    }
}
