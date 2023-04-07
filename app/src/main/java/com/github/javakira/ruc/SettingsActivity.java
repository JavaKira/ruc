package com.github.javakira.ruc;

import static java.lang.Boolean.parseBoolean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;

import com.github.javakira.ruc.adapter.ListAdapter;
import com.github.javakira.ruc.databinding.ActivitySettingsFirstBinding;
import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.Group;
import com.github.javakira.ruc.model.Kit;
import com.github.javakira.ruc.model.SpinnerItem;
import com.github.javakira.ruc.parser.HtmlScheduleParser;
import com.github.javakira.ruc.parser.ScheduleParser;
import com.github.javakira.ruc.utils.FileIO;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsFirstBinding binding;
    private ScheduleParser rucParser;
    private Handler handler;

    private ListAdapter branchAdapter;
    private ListAdapter employeeAdapter;
    private ListAdapter kitAdapter;
    private ListAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsFirstBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Properties properties = FileIO.loadProps(binding.getRoot().getContext());
        rucParser = new HtmlScheduleParser();
        handler = new Handler(Looper.getMainLooper());

        SwitchCompat switchCompat = binding.switch2;
        boolean checked = parseBoolean(properties.getProperty("isEmployee"));
        switchCompat.setChecked(checked);
        if (checked)
            setEmployee();
        else
            setStudent();

        switchCompat.setOnCheckedChangeListener(((compoundButton, b) -> {
            handler.post(() -> {
                if (b) setEmployee();
                else setStudent();
            });

            properties.setProperty("isEmployee", String.valueOf(b));
            FileIO.writeProps(binding.getRoot().getContext(), properties);
        }));

        binding.spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Group item = (Group) groupAdapter.getItem(i);
                properties.setProperty("group", item.getValue());
                FileIO.writeProps(binding.getRoot().getContext(), properties);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerKit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Kit item = (Kit) kitAdapter.getItem(i);
                properties.setProperty("kit", item.getValue());
                FileIO.writeProps(binding.getRoot().getContext(), properties);
                updateGroups(new Branch(null, properties.getProperty("branch")), item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerItem item = employeeAdapter.getItem(i);
                properties.setProperty("employee", item.getValue());
                FileIO.writeProps(binding.getRoot().getContext(), properties);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Branch item = (Branch) branchAdapter.getItem(i);
                properties.setProperty("branch", item.getValue());
                FileIO.writeProps(binding.getRoot().getContext(), properties);
                if (parseBoolean(properties.getProperty("isEmployee"))) {
                    updateEmployees(item);
                } else {
                    updateKits(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        updateBranches();

        binding.settingsFirstButton.setOnClickListener(view -> {
            if (binding.spinnerBranch.getSelectedItemPosition() == 0)
                return;

            if (checked) {
                if (binding.spinnerEmployee.getSelectedItemPosition() == 0)
                    return;
            } else {
                if (binding.spinnerKit.getSelectedItemPosition() == 0)
                    return;

                if (binding.spinnerGroup.getSelectedItemPosition() == 0)
                    return;
            }

            Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void setStudent() {
        binding.spinnerKit.setVisibility(View.VISIBLE);
        binding.spinnerGroup.setVisibility(View.VISIBLE);
        binding.spinnerEmployee.setVisibility(View.GONE);
        updateBranches();
    }

    public void setEmployee() {
        binding.spinnerKit.setVisibility(View.GONE);
        binding.spinnerGroup.setVisibility(View.GONE);
        binding.spinnerEmployee.setVisibility(View.VISIBLE);
        updateBranches();
    }

    private void updateBranches() {
        Properties properties = FileIO.loadProps(binding.getRoot().getContext());
        rucParser.getBranches().thenAccept(branches -> {
            handler.post(() -> {
                List<SpinnerItem> itemList = branches.stream().map(branch -> (SpinnerItem) branch).collect(Collectors.toList());
                itemList.add(0, Branch.empty);
                branchAdapter = new ListAdapter(binding.getRoot().getContext(), itemList);
                binding.spinnerBranch.setAdapter(branchAdapter);
                binding.spinnerBranch.setSelection(
                        branchAdapter.getItemList().indexOf(
                                branchAdapter.getItemList().stream()
                                        .filter(item -> item.getValue().equals(properties.getProperty("branch")))
                                        .findAny()
                                        .orElse(branchAdapter.getItem(0))
                        )
                );
                //todo add default value via binding.spinnerBranch.setSelection(0);
            });
        });
    }

    private void updateEmployees(Branch branch) {
        Properties properties = FileIO.loadProps(binding.getRoot().getContext());
        rucParser.getEmployees(branch.getValue()).thenAccept(employees -> {
            handler.post(() -> {
                List<SpinnerItem> itemList = employees.stream().map(employee -> (SpinnerItem) employee).collect(Collectors.toList());
                itemList.add(0, Employee.empty);
                employeeAdapter = new ListAdapter(binding.getRoot().getContext(), itemList);
                binding.spinnerEmployee.setAdapter(employeeAdapter);
                binding.spinnerEmployee.setSelection(
                        employeeAdapter.getItemList().indexOf(
                                employeeAdapter.getItemList().stream()
                                        .filter(item -> item.getValue().equals(properties.getProperty("employee")))
                                        .findAny()
                                        .orElse(employeeAdapter.getItem(0))
                        )
                );
            });
        });
    }

    private void updateKits(Branch branch) {
        Properties properties = FileIO.loadProps(binding.getRoot().getContext());
        rucParser.getKits(branch.getValue()).thenAccept(kits -> {
            handler.post(() -> {
                List<SpinnerItem> itemList = kits.stream().map(kit -> (SpinnerItem) kit).collect(Collectors.toList());
                itemList.add(0, Kit.empty);
                kitAdapter = new ListAdapter(binding.getRoot().getContext(), itemList);
                binding.spinnerKit.setAdapter(kitAdapter);
                binding.spinnerKit.setSelection(
                        kitAdapter.getItemList().indexOf(
                                kitAdapter.getItemList().stream()
                                        .filter(item -> item.getValue().equals(properties.getProperty("kit")))
                                        .findAny()
                                        .orElse(kitAdapter.getItem(0))
                        )
                );
            });
        });
    }

    private void updateGroups(Branch branch, Kit kit) {
        Properties properties = FileIO.loadProps(binding.getRoot().getContext());
        rucParser.getGroups(branch.getValue(), kit.getValue()).thenAccept(groups -> {
            handler.post(() -> {
                List<SpinnerItem> itemList = groups.stream().map(group -> (SpinnerItem) group).collect(Collectors.toList());
                itemList.add(0, Group.empty);
                groupAdapter = new ListAdapter(binding.getRoot().getContext(), itemList);
                binding.spinnerGroup.setAdapter(groupAdapter);
                binding.spinnerGroup.setSelection(
                        groupAdapter.getItemList().indexOf(
                                groupAdapter.getItemList().stream()
                                        .filter(item -> item.getValue().equals(properties.getProperty("group")))
                                        .findAny()
                                        .orElse(groupAdapter.getItem(0))
                        )
                );
            });
        });
    }
}