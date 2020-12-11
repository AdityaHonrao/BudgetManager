package com.sovereign.budgetmanager.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sovereign.budgetmanager.Database.TransactionDatabaseHelper;
import com.sovereign.budgetmanager.Database.TransactionModel;
import com.sovereign.budgetmanager.ListItemAdapter;
import com.sovereign.budgetmanager.R;

import java.util.Calendar;
import java.util.List;

public class LedgerFragment extends Fragment {
    TransactionDatabaseHelper transactionDatabaseHelper;
    RecyclerView transactionList;
    ListItemAdapter listItemAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEditor;
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    TextView homeExpense, homeIncome, homeCashBal, homeAccountsBal;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_ledger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getContext().getSharedPreferences("OverviewValues", Context.MODE_PRIVATE);
        myEditor = sharedPreferences.edit();
        if (!sharedPreferences.contains("Created")){
            myEditor.putBoolean("Created", true);
            myEditor.putInt("Cash", 0);
            myEditor.putInt("Accounts", 0);
            myEditor.putInt("Income", 0);
            myEditor.putInt("Expenses", 0);
            myEditor.commit();
        }

        FloatingActionButton fab = view.findViewById(R.id.addTransaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionDialog().show();
            }
        });

        homeIncome = view.findViewById(R.id.homeIncome);
        homeExpense = view.findViewById(R.id.homeExpense);
        homeCashBal = view.findViewById(R.id.homeCashBal);
        homeAccountsBal = view.findViewById(R.id.homeAccountsBal);

        updateOverview();


        if (sharedPreferences.contains("Created")){
            homeIncome.setText("Rs "+ sharedPreferences.getInt("Income", -1));
            homeExpense.setText("Rs "+ sharedPreferences.getInt("Expenses", -1));

            int cash = sharedPreferences.getInt("Cash", -1);
            int accounts = sharedPreferences.getInt("Accounts", -1);

            homeCashBal.setTextColor(getResources().getColor(cash>=0 ? R.color.listItemAmountCredit : R.color.listItemAmountDebit, getContext().getTheme()));
            homeAccountsBal.setTextColor(getResources().getColor(accounts>=0 ? R.color.listItemAmountCredit : R.color.listItemAmountDebit, getContext().getTheme()));

            homeCashBal.setText("Rs "+ cash);
            homeAccountsBal.setText("Rs "+ accounts);
        }

        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                homeIncome.setText("Rs "+ sharedPreferences.getInt("Income", -1));
                homeExpense.setText("Rs "+ sharedPreferences.getInt("Expenses", -1));
                int cash = sharedPreferences.getInt("Cash", -1);
                int accounts = sharedPreferences.getInt("Accounts", -1);

                homeCashBal.setTextColor(getResources().getColor(cash>0 ? R.color.listItemAmountCredit : R.color.listItemAmountDebit, getContext().getTheme()));
                homeAccountsBal.setTextColor(getResources().getColor(accounts>0 ? R.color.listItemAmountCredit : R.color.listItemAmountDebit, getContext().getTheme()));

                homeCashBal.setText("Rs "+ cash);
                homeAccountsBal.setText("Rs "+ accounts);
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        transactionDatabaseHelper = new TransactionDatabaseHelper(getContext());
        transactionList = view.findViewById(R.id.transactionList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        transactionList.setLayoutManager(linearLayoutManager);
        listItemAdapter = new ListItemAdapter(getContext());
        transactionList.setAdapter(listItemAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        transactionDatabaseHelper.close();
    }

    public void updateOverview(){
        TransactionDatabaseHelper dbHelper = new TransactionDatabaseHelper(getContext());
        List<TransactionModel> transactionModelList = dbHelper.getAll();

        int catDebitSum = 0;
        int catCreditSum = 0;

        int cash = 0;
        int accounts = 0;

        String[] Type = getContext().getResources().getStringArray(R.array.IncomeType);

        for (TransactionModel t: transactionModelList){
            if (t.isCredit()){
                catCreditSum += t.getAmount();
                if (Type[t.getTransactionMode()].equals("Cash")){
                    cash += t.getAmount();
                } else {
                    accounts += t.getAmount();
                }
            } else {
                catDebitSum += t.getAmount();
                if (Type[t.getTransactionMode()].equals("Cash")){
                    cash -= t.getAmount();
                } else {
                    accounts -= t.getAmount();
                }
            }
        }
        myEditor.putInt("Cash", cash);
        myEditor.putInt("Accounts", accounts);
        myEditor.putInt("Income", catCreditSum);
        myEditor.putInt("Expenses", catDebitSum);
        myEditor.commit();

        homeIncome.setText("Rs "+ sharedPreferences.getInt("Income", -1));
        homeExpense.setText("Rs "+ sharedPreferences.getInt("Expenses", -1));

        homeCashBal.setTextColor(getResources().getColor(cash>0 ? R.color.listItemAmountCredit : R.color.listItemAmountDebit, getContext().getTheme()));
        homeAccountsBal.setTextColor(getResources().getColor(accounts>0 ? R.color.listItemAmountCredit : R.color.listItemAmountDebit, getContext().getTheme()));

        homeCashBal.setText("Rs "+ sharedPreferences.getInt("Cash", 0));
        homeAccountsBal.setText("Rs "+ sharedPreferences.getInt("Accounts", 0));
    }

    public void updateList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        transactionList.setLayoutManager(linearLayoutManager);
        listItemAdapter = new ListItemAdapter(getContext());
        transactionList.setAdapter(listItemAdapter);
    }



    private Dialog transactionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater layoutInflater = getLayoutInflater();

        final View dialogView = layoutInflater.inflate(R.layout.transaction_dialog, null);

        final EditText transactionDate = dialogView.findViewById(R.id.transactionDate);
        final EditText transactionTime = dialogView.findViewById(R.id.transactionTime);

        transactionDate.setInputType(InputType.TYPE_NULL);

        Calendar calendar = Calendar.getInstance();
        final String date, time;
        date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);
        time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        transactionDate.setText(date);
        transactionTime.setText(time);

        transactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =  new DatePickerDialog(getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        transactionDate.setText(dayOfMonth + "/" + month + "/" + year);

                        boolean is24HourView = false;
                    }
                });
                datePickerDialog.show();
            }
        });

        final int currentMinute = calendar.get(Calendar.MINUTE);
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        transactionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        transactionTime.setText(hourOfDay + ":" + minute);
                    }
                }, currentHour, currentMinute, true);
                timePickerDialog.show();
            }
        });

        CheckBox check = dialogView.findViewById(R.id.transactionType);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    final Spinner spinnero = dialogView.findViewById(R.id.catSpinner);
                    spinnero.setVisibility(View.GONE);
                } else {
                    final Spinner spinnero = dialogView.findViewById(R.id.catSpinner);
                    spinnero.setVisibility(View.VISIBLE);
                }
            }
        });


        final Spinner spinner = dialogView.findViewById(R.id.catSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.ExpenseCats, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Spinner transactionMode = dialogView.findViewById(R.id.transactionMode);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.IncomeType, android.R.layout.simple_spinner_item);
        transactionMode.setAdapter(adapter1);

        builder.setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Test", "catSpinner.getSelectedItem().toString()");

                        EditText transactionTitle = dialogView.findViewById(R.id.transactionTitle);
                        EditText transactionAmount = dialogView.findViewById(R.id.transactionAmount);
                        CheckBox checkBox = dialogView.findViewById(R.id.transactionType);


                        TransactionDatabaseHelper transactionDatabaseHelper = new TransactionDatabaseHelper(getContext());
                        TransactionModel transactionModel = new TransactionModel();
                        transactionModel.setTitle(transactionTitle.getText().toString());
                        transactionModel.setAmount(Integer.parseInt(transactionAmount.getText().toString()));
                        transactionModel.setCat(spinner.getSelectedItemPosition());
                        transactionModel.setCredit(checkBox.isChecked());
                        transactionModel.setTransactionMode(transactionMode.getSelectedItemPosition());
                        transactionModel.setDate(transactionDate.getText().toString());
                        transactionModel.setTime(transactionTime.getText().toString());

                        boolean success = transactionDatabaseHelper.addTransaction(transactionModel);
                        updateOverview();
                        updateList();


                    }
                });
        return builder.create();
    }
}