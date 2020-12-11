package com.sovereign.budgetmanager.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sovereign.budgetmanager.Database.LimitDatabaseHelper;
import com.sovereign.budgetmanager.Database.LimitModel;
import com.sovereign.budgetmanager.Database.TransactionDatabaseHelper;
import com.sovereign.budgetmanager.Database.TransactionModel;
import com.sovereign.budgetmanager.LimitItemAdapter;
import com.sovereign.budgetmanager.ListItemAdapter;
import com.sovereign.budgetmanager.R;

import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;

    FloatingActionButton addLimit;

    TransactionDatabaseHelper transactionDatabaseHelper;
    LimitDatabaseHelper limitDatabaseHelper;
    LimitItemAdapter limitItemAdapter;

    RecyclerView limitList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionDatabaseHelper = new TransactionDatabaseHelper(getContext());
        limitDatabaseHelper = new LimitDatabaseHelper(getContext());

        pieChart = view.findViewById(R.id.pieChart);
        getEntries();
        pieDataSet = new PieDataSet(pieEntries, "");
        pieData = new PieData(pieDataSet);


        Description description = new Description();
        description.setText("");
        pieChart.setCenterTextColor(getResources().getColor(R.color.BgColorDark, getContext().getTheme()));
        pieChart.setDescription(description);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(5);
        pieChart.setTransparentCircleRadius(0);
        pieChart.animateX(500);
        pieChart.setRotationEnabled(false);
        pieChart.setMinAngleForSlices(15f);
        pieChart.setBackgroundColor(getResources().getColor(R.color.listItemBg, getContext().getTheme()));

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        addLimit = view.findViewById(R.id.addLimit);
        addLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limitDialog().show();
            }
        });


        limitList = view.findViewById(R.id.limitsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        limitList.setLayoutManager(linearLayoutManager);
        limitItemAdapter = new LimitItemAdapter(getContext());
        //limitList.setAdapter(limitItemAdapter);

    }


    private void getEntries() {
        pieEntries = new ArrayList<>();

        List<TransactionModel> transactionModelList = transactionDatabaseHelper.getAll();

        String[] expenseCats = getResources().getStringArray(R.array.ExpenseCats);
        String[] incomeCats = getResources().getStringArray(R.array.IncomeType);

        int[] catDebitSum = new int[expenseCats.length];
        int[] catCreditSum = new int[incomeCats.length];


        for (int i: catDebitSum){
            i = 0;
        }
        for (int i: catCreditSum){
            i = 0;
        }

        for (TransactionModel t: transactionModelList){
            if (t.getAmount()!=0){
                if (t.isCredit()){
                    catCreditSum[t.getCat()] += t.getAmount();
                } else {
                    catDebitSum[t.getCat()] += t.getAmount();
                }
            }
        }

        for (int i=0; i<catDebitSum.length; i++){
            if (catDebitSum[i]!=0){
                pieEntries.add(new PieEntry(catDebitSum[i], expenseCats[i]));
            }
        }
    }

    private Dialog limitDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater layoutInflater = getLayoutInflater();

        final View dialogView = layoutInflater.inflate(R.layout.limit_dialog, null);

        CheckBox check = dialogView.findViewById(R.id.limitType);

        final Spinner spinner = dialogView.findViewById(R.id.limitCat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.ExpenseCats, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText limitText = dialogView.findViewById(R.id.limit);

                        int limit, cat, type;
                        limit = Integer.parseInt(limitText.getText().toString());
                        cat = spinner.getSelectedItemPosition();
                        type = (check.isChecked()? 1:0);

                        LimitModel limitModel = new LimitModel(limit, cat, type);
                        limitDatabaseHelper.addLimit(limitModel);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }
}
