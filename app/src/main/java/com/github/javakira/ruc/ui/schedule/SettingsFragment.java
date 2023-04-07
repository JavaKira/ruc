package com.github.javakira.ruc.ui.schedule;

import static java.lang.Boolean.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.github.javakira.ruc.adapter.ListAdapter;
import com.github.javakira.ruc.model.Group;
import com.github.javakira.ruc.model.Kit;
import com.github.javakira.ruc.parser.ScheduleParser;
import com.github.javakira.ruc.utils.FileIO;
import com.github.javakira.ruc.databinding.FragmentSettingsBinding;
import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.SpinnerItem;
import com.github.javakira.ruc.parser.HtmlScheduleParser;

import java.util.Properties;
import java.util.stream.Collectors;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private ScheduleParser rucParser;
    private Handler handler;

    private ListAdapter branchAdapter;
    private ListAdapter employeeAdapter;
    private ListAdapter kitAdapter;
    private ListAdapter groupAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Properties properties = FileIO.loadProps(getContext());
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
            if (b) setEmployee();
            else setStudent();

            properties.setProperty("isEmployee", String.valueOf(b));
            FileIO.writeProps(requireContext(), properties);
        }));

        binding.spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Group item = (Group) groupAdapter.getItem(i);
                properties.setProperty("group", item.getValue());
                FileIO.writeProps(requireContext(), properties);
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
                FileIO.writeProps(requireContext(), properties);
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
                FileIO.writeProps(requireContext(), properties);
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
                FileIO.writeProps(requireContext(), properties);
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
        Properties properties = FileIO.loadProps(getContext());
        rucParser.getBranches().thenAccept(branches -> {
            handler.post(() -> {
                branchAdapter = new ListAdapter(binding.getRoot().getContext(), branches.stream().map(branch -> (SpinnerItem) branch).collect(Collectors.toList()));
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
        Properties properties = FileIO.loadProps(getContext());
        rucParser.getEmployees(branch.getValue()).thenAccept(employees -> {
            handler.post(() -> {
                employeeAdapter = new ListAdapter(binding.getRoot().getContext(), employees.stream().map(employee -> (SpinnerItem) employee).collect(Collectors.toList()));
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
        Properties properties = FileIO.loadProps(getContext());
        rucParser.getKits(branch.getValue()).thenAccept(kits -> {
            handler.post(() -> {
                kitAdapter = new ListAdapter(binding.getRoot().getContext(), kits.stream().map(kit -> (SpinnerItem) kit).collect(Collectors.toList()));
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
        Properties properties = FileIO.loadProps(getContext());
        rucParser.getGroups(branch.getValue(), kit.getValue()).thenAccept(groups -> {
            handler.post(() -> {
                groupAdapter = new ListAdapter(binding.getRoot().getContext(), groups.stream().map(group -> (SpinnerItem) group).collect(Collectors.toList()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
