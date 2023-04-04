package com.github.javakira.ruc.parser;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Card;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.Group;
import com.github.javakira.ruc.model.Kit;
import com.github.javakira.ruc.model.Pair;
import com.github.javakira.ruc.model.SpinnerItem;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RucParser {
    public static String link = "https://schedule.ruc.su/";
    public static String employeeLink = "https://schedule.ruc.su/employee/";

    public void useBranches(Consumer<List<Branch>> post) {
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

            handler.post(() -> {
                List<Branch> branchList = new ArrayList<>();
                elements.forEach(element -> branchList.add(new Branch(element.text(), element.attr("value"))));
                post.accept(branchList);
            });
        });
    }

    public void useKits(String branch, Consumer<Kit> post) {
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
                        .filter(element -> element.attr("name").equals("year"))
                        .collect(Collectors.toList()).get(0);
                elements = employee.children();
                elements.remove(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> elements.forEach(element -> post.accept(new Kit(element.text(), element.attr("value")))));
        });
    }

    public void useGroups(String branch, String kit, Consumer<Group> post) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Elements elements;

            try {
                Connection connection = Jsoup.connect(employeeLink);
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                data.put("year", kit);
                connection.data(data);
                Document document = connection.post();
                Element employee = document.getElementsByAttribute("name").stream()
                        .filter(element -> element.attr("name").equals("group"))
                        .collect(Collectors.toList()).get(0);
                elements = employee.children();
                elements.remove(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> elements.forEach(element -> post.accept(new Group(element.text(), element.attr("value")))));
        });
    }

    public void useEmployees(String branch, Consumer<Employee> post) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Elements elements;

            try {
                Connection connection = Jsoup.connect(employeeLink);
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

            handler.post(() -> elements.forEach(element -> post.accept(new Employee(element.text(), element.attr("value")))));
        });
    }

    public void useGroupCards(String branch, String kit, String group, Consumer<Card> post) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        List<Card> cardList = new ArrayList<>();

        executor.execute(() -> {
            try {
                Connection connection = Jsoup.connect(link);
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                data.put("year", kit);
                data.put("group", group);
                connection.data(data);
                Document document = connection.post();

                Elements cards = document.getElementsByClass("card");
                for (Element cardElement : cards) {
                    List<Pair> pairList = new ArrayList<>();
                    if (cards.size() <= 1)
                        return;

                    Elements pairs = cardElement.children();
                    Element header = pairs.get(0);
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

                    String strDate = header.text().split(" ")[0];
                    SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                    cardList.add(new Card(parser.parse(strDate), pairList));
                }
            } catch (Exception e) {
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                data.put("year", kit);
                data.put("group", group);
                Log.e("RUC RucParser ", data + " : " + e);
            }

            handler.post(() -> cardList.forEach(post));
        });
    }

    public void useEmployeeCards(String branch, String employee, Consumer<Card> post) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        List<Card> cardList = new ArrayList<>();

        executor.execute(() -> {
            try {
                Connection connection = Jsoup.connect(employeeLink);
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                data.put("employee", employee);
                connection.data(data);
                Document document = connection.post();

                Elements cards = document.getElementsByClass("card");
                for (Element cardElement : cards) {
                    List<Pair> pairList = new ArrayList<>();
                    if (cards.size() <= 1)
                        return;

                    Elements pairs = cardElement.children();
                    Element header = pairs.get(0);
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

                    String strDate = header.text().split(" ")[0];
                    SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                    cardList.add(new Card(parser.parse(strDate), pairList));
                }
            } catch (Exception e) {
                HashMap<String, String> data = new HashMap<>();
                data.put("branch", branch);
                data.put("employee", employee);
                Log.e("RUC RucParser ", data + " : " + e);
            }

            handler.post(() -> cardList.forEach(post));
        });
    }
}
