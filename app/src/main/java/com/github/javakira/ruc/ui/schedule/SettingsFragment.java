package com.github.javakira.ruc.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.javakira.ruc.utils.FileIO;
import com.github.javakira.ruc.R;
import com.github.javakira.ruc.SpinnerFacade;
import com.github.javakira.ruc.databinding.FragmentSettingsBinding;
import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.SpinnerItem;
import com.github.javakira.ruc.parser.RucParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

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
        Properties properties = FileIO.loadProps(getContext());

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
                    items1.clear();
                    RucParser.useEmployees(item.getValue(), (Consumer<Employee>) employee -> {
                        items1.add(employee);
                        employeeSpinnerFacade.updateBranchItemWithoutInvoke(items1.stream().filter(item2 -> item2.getValue().equals(properties.getProperty("employee"))).findAny().orElse(Employee.empty));
                    });
                });

        RucParser.useBranches((Consumer<List<Branch>>) branchList -> {
            items.addAll(branchList);
            branchSpinnerFacade.updateBranchItem(branchList.stream().filter(item -> item.getValue().equals(properties.getProperty("branch"))).findAny().orElse(Branch.empty));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
