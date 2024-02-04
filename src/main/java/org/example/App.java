package org.example;

import com.google.gson.Gson;

import javax.sound.midi.Patch;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.parse;

public class App {
    private static String path = "/Users/ROG/Desktop/access.log";
    private static String output = "/Users/ROG/Desktop/result.json";
    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
    private static String dateTimeRegex = ".+\\[([^\\]]{20}\\s\\+[0-9]{4})\\].+";

    public static void main(String[] args) throws IOException {
        Pattern dateTimPattern = Pattern.compile(dateTimeRegex);

        HashMap<Long, Integer> countPerSecond = new HashMap<>();

        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;

        int requestsCount = 0;

        List<String> lines = Files.readAllLines(Paths.get(path));
        for (String line : lines) {
            Matcher mather = dateTimPattern.matcher(line);
            if (!mather.find()) {
                continue;
            }
            String dateTime = mather.group(1);
            long time = getTimestamp(dateTime);

            if (!countPerSecond.containsKey(time)) {
                countPerSecond.put(time, 0);
            }
            countPerSecond.put(time,
                    countPerSecond.get(time) + 1);

            if (!countPerSecond.containsKey(time)) {
                countPerSecond.put(time, 0);
            }

            minTime = Math.min(time, minTime);
            maxTime = Math.max(time, maxTime);
            requestsCount++;
        }
        int maxRequstsSecond = Collections.max(countPerSecond.values());

        double averageRequestsPerSecond = (double) requestsCount / (maxTime - minTime);
//        LocalDateTime time2 = LocalDateTime.of(2020, 12, 5, 6, 7, 10);
//        System.out.println(time2.toEpochSecond(ZoneOffset.UTC));
        Statistics statistics =
                new Statistics(maxRequstsSecond, averageRequestsPerSecond);

        Gson gson = new Gson();
        String json = gson.toJson(statistics);

        FileWriter writer = new FileWriter(output);
        writer.write(json);
        writer.flush();
        writer.close();

        //System.out.println(json);
    }

    public static long getTimestamp(String dateTime) {
        LocalDateTime time = LocalDateTime.parse(dateTime, formatter);
        //System.out.println(time);
        return time.toEpochSecond(ZoneOffset.UTC);
    }
}
