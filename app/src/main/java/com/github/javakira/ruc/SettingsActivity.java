package com.github.javakira.ruc;

import static java.lang.Boolean.parseBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.github.javakira.ruc.databinding.ActivitySettingsFirstBinding;
import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.Group;
import com.github.javakira.ruc.model.Kit;
import com.github.javakira.ruc.model.SpinnerItem;
import com.github.javakira.ruc.parser.HtmlScheduleParser;
import com.github.javakira.ruc.parser.ScheduleParser;
import com.github.javakira.ruc.utils.FileIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsFirstBinding binding;
    private ScheduleParser rucParser;
    private Handler handler;
    private SpinnerFacade branchSpinnerFacade;
    private List<SpinnerItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsFirstBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        items = new ArrayList<>();
        List<SpinnerItem> items1 = new ArrayList<>();
        List<SpinnerItem> items2 = new ArrayList<>();
        List<SpinnerItem> items3 = new ArrayList<>();
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

        SpinnerFacade employeeSpinnerFacade = new SpinnerFacade(
                binding.settingsEmployeeRecycler,
                binding.settingsEmployeeTitle,
                binding.settingsEmployee,
                Employee.empty,
                binding.getRoot().getContext(),
                items1,
                item -> {
                    properties.setProperty("employee", item.getValue());
                    FileIO.writeProps(binding.getRoot().getContext(), properties);
                });

        SpinnerFacade groupSpinnerFacade = new SpinnerFacade(
                binding.settingsGroupRecycler,
                binding.settingsGroupTitle,
                binding.settingsGroup,
                Group.empty,
                binding.getRoot().getContext(),
                items3,
                item -> {
                    properties.setProperty("group", item.getValue());
                    FileIO.writeProps(binding.getRoot().getContext(), properties);
                }
        );

        SpinnerFacade kitSpinnerFacade = new SpinnerFacade(
                binding.settingsKitRecycler,
                binding.settingsKitTitle,
                binding.settingsKit,
                Kit.empty,
                binding.getRoot().getContext(),
                items2,
                item -> {
                    properties.setProperty("kit", item.getValue());
                    FileIO.writeProps(binding.getRoot().getContext(), properties);
                    rucParser.getGroups(properties.getProperty("branch"), item.getValue()).thenAccept((groups) -> {
                        handler.post(() -> {
                            items3.clear();
                            items3.addAll(groups);
                            groupSpinnerFacade.updateBranchItemWithoutInvoke(items3.stream().filter(item1 -> item1.getValue().equals(properties.getProperty("group"))).findAny().orElse(Group.empty));
                        });
                    });
                }
        );

        branchSpinnerFacade = new SpinnerFacade(
                binding.settingsBranchRecycler,
                binding.settingsBranchTitle,
                binding.spinnerTitle,
                Branch.empty,
                binding.getRoot().getContext(),
                items,
                item -> {
                    properties.setProperty("branch", item.getValue());
                    FileIO.writeProps(binding.getRoot().getContext(), properties);
                    if (parseBoolean(properties.getProperty("isEmployee"))) {
                        items1.clear();
                        rucParser.getEmployees(item.getValue()).thenAccept(employees -> {
                            handler.post(() -> {
                                items1.addAll(employees);
                                employeeSpinnerFacade.updateBranchItemWithoutInvoke(items1.stream().filter(item1 -> item1.getValue().equals(properties.getProperty("employee"))).findAny().orElse(Employee.empty));
                            });
                        });
                    } else {
                        items2.clear();
                        rucParser.getKits(item.getValue()).thenAccept(kits -> {
                            handler.post(() -> {
                                items2.addAll(kits);
                                kitSpinnerFacade.updateBranchItem(items2.stream().filter(item2 -> item2.getValue().equals(properties.getProperty("kit"))).findAny().orElse(Kit.empty));
                            });
                        });
                    }
                });

        updateBranches();

        binding.settingsFirstButton.setOnClickListener(view -> {
            if (branchSpinnerFacade.getItem() == Branch.empty)
                return;

            if (checked) {
                if (employeeSpinnerFacade.getItem() == Employee.empty)
                    return;
            } else {
                if (kitSpinnerFacade.getItem() == Kit.empty)
                    return;

                if (groupSpinnerFacade.getItem() == Group.empty)
                    return;
            }

            Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void setStudent() {
        binding.settingsKit.setVisibility(View.VISIBLE);
        binding.settingsGroup.setVisibility(View.VISIBLE);
        binding.settingsEmployee.setVisibility(View.GONE);
        updateBranches();
    }

    public void setEmployee() {
        binding.settingsKit.setVisibility(View.GONE);
        binding.settingsGroup.setVisibility(View.GONE);
        binding.settingsEmployee.setVisibility(View.VISIBLE);
        updateBranches();
    }

    private void updateBranches() {
        Properties properties = FileIO.loadProps(binding.getRoot().getContext());
        rucParser.getBranches().thenAccept((branches) -> {
            handler.post(() -> {
                items.addAll(branches);
                branchSpinnerFacade.updateBranchItem(branches.stream().filter(item -> item.getValue().equals(properties.getProperty("branch"))).findAny().orElse(Branch.empty));
            });
        });
    }
}