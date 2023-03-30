package com.github.javakira.ruc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;

import com.github.javakira.ruc.model.Branch;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.SpinnerItem;
import com.github.javakira.ruc.parser.RucParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

public class SettingsActivity extends AppCompatActivity {
    private SpinnerFacade branchSpinnerFacade;
    private SpinnerFacade employeeSpinnerFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_first);

        List<SpinnerItem> items = new ArrayList<>();
        List<SpinnerItem> items1 = new ArrayList<>();
        RucParser.useBranches((Consumer<List<Branch>>) items::addAll);

        branchSpinnerFacade = new SpinnerFacade(
                findViewById(R.id.settings_first_branch_recycler),
                findViewById(R.id.settings_first_branch_title),
                findViewById(R.id.spinner_title),
                Branch.empty,
                this,
                items,
                item -> {
                    items1.clear();
                    RucParser.useEmployees(item.getValue(), (Consumer<Employee>) items1::add);
                });


        employeeSpinnerFacade = new SpinnerFacade(
                findViewById(R.id.settings_first_employee_recycler),
                findViewById(R.id.settings_first_employee_title),
                findViewById(R.id.settings_first_employee),
                Employee.empty,
                this,
                items1,
                item -> {});

        CardView endButton = findViewById(R.id.settings_first_button);
        endButton.setOnClickListener(view -> {
            if (branchSpinnerFacade.getItem() == Branch.empty)
                return;

            if(employeeSpinnerFacade.getItem() == Employee.empty)
                return;

            Properties properties = FileIO.loadProps("config.txt", this);
            properties.setProperty("branch", branchSpinnerFacade.getItem().getValue());
            properties.setProperty("employee", employeeSpinnerFacade.getItem().getValue());
            FileIO.writeProps("config.txt", this, properties);
            finish();
        });
    }
}