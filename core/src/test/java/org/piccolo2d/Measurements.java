package org.piccolo2d;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Measurements {
    private Map<String, List<Long>> measurements = new HashMap<String, List<Long>>();

    public void time(String name, Runnable runnable) {
        if (!measurements.containsKey(name)) {
            measurements.put(name, new LinkedList<Long>());
        }

        long startTime = System.currentTimeMillis();
        runnable.run();
        long endTime = System.currentTimeMillis();

        measurements.get(name).add(endTime - startTime);
    }

    public void memory(String name, Runnable runnable) {
        if (!measurements.containsKey(name)) {
            measurements.put(name, new LinkedList<Long>());
        }

        Runtime.getRuntime().gc();
        final long startTotalMemory = Runtime.getRuntime().totalMemory();
        final long startFree = Runtime.getRuntime().freeMemory();

        runnable.run();

        Runtime.getRuntime().gc();
        final long endFree = Runtime.getRuntime().freeMemory();
        final long endTotal = Runtime.getRuntime().totalMemory();

        final long memoryUsed = ((endTotal - startTotalMemory + startFree - endFree) / 1024);
        measurements.get(name).add(memoryUsed);
    }

    public void writeLog() {
        System.out.println("name,average,min,max");
        for (Entry<String, List<Long>> entry : measurements.entrySet()) {
            System.out.print('"');
            System.out.print(entry.getKey());
            System.out.print('"');
            System.out.print(", ");
            System.out.print(calculateAverage(entry.getValue()));
            System.out.print(", ");
            System.out.print(calculateMinimum(entry.getValue()));
            System.out.print(", ");
            System.out.print(calculateMaximum(entry.getValue()));
            System.out.println();
        }

        System.out.println();
        System.out.println("Raw Data");
        System.out.println("name, run, value");
        for (Entry<String, List<Long>> entry : measurements.entrySet()) {
            int runCount = 0;
            for (long value : entry.getValue()) {
                System.out.print('"');
                System.out.print(entry.getKey());
                System.out.print('"');
                System.out.print(", ");
                System.out.print(++runCount);
                System.out.print(", ");
                System.out.println(value);
            }
        }
    }

    private long calculateAverage(List<Long> measurements) {
        long total = 0;

        for (Long measurement : measurements)
            total += measurement;

        return total / measurements.size();
    }

    private long calculateMinimum(List<Long> measurements) {
        long min = measurements.get(0);

        for (Long measurement : measurements)
            if (min > measurement)
                min = measurement;

        return min;
    }

    private long calculateMaximum(List<Long> measurements) {
        long max = measurements.get(0);

        for (Long measurement : measurements)
            if (max < measurement)
                max = measurement;

        return max;
    }
}
