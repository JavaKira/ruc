package com.github.javakira.ruc;

import static java.lang.Boolean.parseBoolean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.github.javakira.ruc.databinding.ActivitySettingsFirstBinding;
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

        updateBranches();

//        binding.settingsFirstButton.setOnClickListener(view -> {
//            if (branchSpinnerFacade.getItem() == Branch.empty)
//                return;
//
//            if (checked) {
//                if (employeeSpinnerFacade.getItem() == Employee.empty)
//                    return;
//            } else {
//                if (kitSpinnerFacade.getItem() == Kit.empty)
//                    return;
//
//                if (groupSpinnerFacade.getItem() == Group.empty)
//                    return;
//            }
//
//            Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        });
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
//        Properties properties = FileIO.loadProps(binding.getRoot().getContext());
//        rucParser.getBranches().thenAccept((branches) -> {
//            handler.post(() -> {
//                items.addAll(branches);
//                branchSpinnerFacade.updateBranchItem(branches.stream().filter(item -> item.getValue().equals(properties.getProperty("branch"))).findAny().orElse(Branch.empty));
//            });
//        });
    }
}