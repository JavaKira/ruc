package com.github.javakira.ruc.ui.schedule;

import static java.lang.Boolean.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.github.javakira.ruc.model.Group;
import com.github.javakira.ruc.model.Kit;
import com.github.javakira.ruc.parser.ScheduleParser;
import com.github.javakira.ruc.utils.FileIO;
import com.github.javakira.ruc.R;
import com.github.javakira.ruc.SpinnerFacade;
import com.github.javakira.ruc.databinding.FragmentSettingsBinding;
import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.SpinnerItem;
import com.github.javakira.ruc.parser.HtmlScheduleParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private ScheduleParser rucParser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<SpinnerItem> items = new ArrayList<>();
        List<SpinnerItem> items1 = new ArrayList<>();
        List<SpinnerItem> items2 = new ArrayList<>();
        List<SpinnerItem> items3 = new ArrayList<>();
        Properties properties = FileIO.loadProps(getContext());
        rucParser = new HtmlScheduleParser();

        SwitchCompat switchCompat = binding.switch2;
        boolean checked = parseBoolean(properties.getProperty("isEmployee"));
        switchCompat.setChecked(checked);
        if (checked)
            setEmployee();
        else
            setStudent();

        switchCompat.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) setEmployee();
            else setStudent();

            properties.setProperty("isEmployee", String.valueOf(b));
            FileIO.writeProps(requireContext(), properties);
        }));

        SpinnerFacade employeeSpinnerFacade = new SpinnerFacade(
                view.findViewById(R.id.settings_employee_recycler),
                view.findViewById(R.id.settings_employee_title),
                view.findViewById(R.id.settings_employee),
                Employee.empty,
                getContext(),
                items1,
                item -> {
                    properties.setProperty("employee", item.getValue());
                    FileIO.writeProps(requireContext(), properties);
                });

        SpinnerFacade groupSpinnerFacade = new SpinnerFacade(
                binding.settingsGroupRecycler,
                binding.settingsGroupTitle,
                binding.settingsGroup,
                Group.empty,
                getContext(),
                items3,
                item -> {
                    properties.setProperty("group", item.getValue());
                    FileIO.writeProps(requireContext(), properties);
                }
        );

        SpinnerFacade kitSpinnerFacade = new SpinnerFacade(
                binding.settingsKitRecycler,
                binding.settingsKitTitle,
                binding.settingsKit,
                Kit.empty,
                getContext(),
                items2,
                item -> {
                    properties.setProperty("kit", item.getValue());
                    FileIO.writeProps(requireContext(), properties);
                    rucParser.getGroups(properties.getProperty("branch"), item.getValue()).thenAccept((groups) -> {
                        items3.clear();
                        items3.addAll(groups);
                        groupSpinnerFacade.updateBranchItemWithoutInvoke(items3.stream().filter(item1 -> item1.getValue().equals(properties.getProperty("group"))).findAny().orElse(Group.empty));
                    });
                }
        );

        SpinnerFacade branchSpinnerFacade = new SpinnerFacade(
                view.findViewById(R.id.settings_branch_recycler),
                view.findViewById(R.id.settings_branch_title),
                view.findViewById(R.id.spinner_title),
                Branch.empty,
                getContext(),
                items,
                item -> {
                    properties.setProperty("branch", item.getValue());
                    FileIO.writeProps(requireContext(), properties);
                    if (parseBoolean(properties.getProperty("isEmployee"))) {
                        items1.clear();
                        rucParser.getEmployees(item.getValue()).thenAccept(employees -> {
                            items1.addAll(employees);
                            employeeSpinnerFacade.updateBranchItemWithoutInvoke(items1.stream().filter(item1 -> item1.getValue().equals(properties.getProperty("employee"))).findAny().orElse(Employee.empty));
                        });
                    } else {
                        items2.clear();
                        rucParser.getKits(item.getValue()).thenAccept(kits -> {
                            items2.addAll(kits);
                            kitSpinnerFacade.updateBranchItem(items2.stream().filter(item2 -> item2.getValue().equals(properties.getProperty("kit"))).findAny().orElse(Kit.empty));
                        });
                    }
                });

        rucParser.getBranches().thenAccept((branches) -> {
            items.addAll(branches);
            branchSpinnerFacade.updateBranchItem(branches.stream().filter(item -> item.getValue().equals(properties.getProperty("branch"))).findAny().orElse(Branch.empty));
        });
    }

    public void setStudent() {
        binding.settingsKit.setVisibility(View.VISIBLE);
        binding.settingsGroup.setVisibility(View.VISIBLE);
        binding.settingsEmployee.setVisibility(View.GONE);
    }

    public void setEmployee() {
        binding.settingsKit.setVisibility(View.GONE);
        binding.settingsGroup.setVisibility(View.GONE);
        binding.settingsEmployee.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
