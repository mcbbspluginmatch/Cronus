package ink.ptms.cronus;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author 坏黑
 * @Since 2018-12-24 16:32
 */
public class CronusMirror {

    private static Map<String, Data> dataMap = Maps.newTreeMap();

    public static Map<String, Data> getMirrors() {
        return dataMap;
    }

    public static Data getMirror(String id) {
        return dataMap.computeIfAbsent(id, i -> new Data());
    }

    public static Data getMirror() {
        return new Data();
    }

    public static boolean isIgnored(Class clazz) {
        return clazz.isAnnotationPresent(Ignore.class);
    }

    public static class Data {

        private double timeTotal;
        private double timeLatest;
        private long times;
        private long runtime;

        public void start() {
            runtime = System.nanoTime();
        }

        public void stop() {
            timeLatest = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runtime);
            timeTotal += timeLatest;
            times += 1;
        }

        public void reset() {
            timeTotal = 0;
            timeLatest = 0;
            times = 0;
        }

        // *********************************
        //
        //        Getter and Setter
        //
        // *********************************

        public double getTimeTotal() {
            return timeTotal;
        }

        public double getTimeLatest() {
            return timeLatest;
        }

        public long getTimes() {
            return times;
        }
    }
}
