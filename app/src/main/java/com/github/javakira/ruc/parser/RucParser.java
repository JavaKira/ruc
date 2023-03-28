package com.github.javakira.ruc.parser;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.Pair;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RucParser {
    public static String link = "https://schedule.ruc.su/employee/";

    public static void useBranches(Function<Branch, Void> post) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Elements elements;

            try {
                Connection connection = Jsoup.connect(link);
                Document document = connection.post();
                Element employee = document.getElementsByAttribute("name").stream()
                        .filter(element -> element.attr("name").equals("branch"))
                        .collect(Collectors.toList()).get(0);
                elements = employee.children();
                elements.remove(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> elements.forEach(element -> post.apply(new Branch(element.text(), element.attr("value")))));
        });
    }

    public static void useEmployees(String branch, Function<Employee, Void> post) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Elements elements;

            try {
                Connection connection = Jsoup.connect(link);
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                connection.data(data);
                Document document = connection.post();
                Element employee = document.getElementsByAttribute("name").stream()
                        .filter(element -> element.attr("name").equals("employee"))
                        .collect(Collectors.toList()).get(0);
                elements = employee.children();
                elements.remove(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> elements.forEach(element -> post.apply(new Employee(element.text(), element.attr("value")))));
        });
    }

    public static void usePairs(String branch, Date date, String employee, Function<Pair, Void> post) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        List<Pair> pairList = new ArrayList<>();

        executor.execute(() -> {
            try {
                Connection connection = Jsoup.connect(link);
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                data.put("employee", employee);
                connection.data(data);
                Document document = connection.post();

                Elements cards = document.getElementsByClass("card");
                if (cards.size() <= 1)
                    return;

                Elements pairs = cards.get(1).children();
                pairs.remove(0);


                for (Element element : pairs) {
                    String pairName = element.children().first().text();
                    String text = element.toString().replace(pairName, "").replace("Группа", "").trim();
                    Matcher matcher = Pattern.compile("[0-9].").matcher(pairName);
                    matcher.find();
                    int pairIndex = Integer.parseInt(pairName.substring(matcher.start(), matcher.end() - 1));
                    pairName = pairName.replaceAll("[0-9].", "");
                    String[] split = text.split("<br>");
                    String[] split1 = split[2].split(",");

                    pairList.add(new Pair(
                            pairIndex - 1,
                            pairName.trim(),
                            split[1].trim(),
                            split1[0].trim(),
                            split1[1].trim(),
                            split[1].trim()
                    ));
                }
            } catch (IOException e) {
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                data.put("employee", employee);
                Log.e("RUC RucParser ", data + " : " + e);
            }

            handler.post(() -> pairList.forEach(post::apply));
        });
    }
}
