package com.github.javakira.ruc.parser;

import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Card;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.Group;
import com.github.javakira.ruc.model.Kit;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ScheduleParser {
    String link = "https://schedule.ruc.su/";
    String employeeLink = "https://schedule.ruc.su/employee/";

    CompletableFuture<List<Branch>> getBranches();
    CompletableFuture<List<Kit>> getKits(String branch);
    CompletableFuture<List<Group>> getGroups(String branch, String kit);
    CompletableFuture<List<Employee>> getEmployees(String branch);
    CompletableFuture<List<Card>> getGroupCards(String branch, String kit, String group);
    CompletableFuture<List<Card>> getEmployeeCards(String branch, String employee);
}
