package com.sovereign.budgetmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sovereign.budgetmanager.Database.TransactionDatabaseHelper;
import com.sovereign.budgetmanager.Database.TransactionModel;

import java.util.Collections;
import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {


    private Context mContext;
    private List<TransactionModel> transactionModelList;

    TransactionDatabaseHelper transactionDatabaseHelper;

    public ListItemAdapter(Context mContext) {
        this.mContext = mContext;


        transactionDatabaseHelper = new TransactionDatabaseHelper(mContext);
        transactionModelList = transactionDatabaseHelper.getAll();
        Collections.reverse(transactionModelList);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.single_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionModel transactionModel = transactionModelList.get(position);

        holder.listItemTitle.setText(transactionModel.getTitle());
        holder.listItemAmount.setTextColor(mContext.getResources()
                .getColor((transactionModel.isCredit() ? R.color.listItemAmountCredit : R.color.listItemAmountDebit),
                        mContext.getTheme()));

        holder.listItemAmount.setText(String.valueOf(transactionModel.getAmount()));
        String[] cats = mContext.getResources().getStringArray(transactionModel.isCredit() ? R.array.IncomeType : R.array.ExpenseCats);
        holder.listItemCat.setText(cats[transactionModel.getCat()]);
        holder.listItemDate.setText(transactionModel.getDate() + " " + transactionModel.getTime());
    }

    @Override
    public int getItemCount() {
        return transactionModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView listItemTitle;
        public TextView listItemAmount;
        public TextView listItemCat;
        public TextView listItemDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemTitle = itemView.findViewById(R.id.limitItemCat);
            listItemAmount = itemView.findViewById(R.id.listItemAmount);
            listItemCat = itemView.findViewById(R.id.listItemCat);
            listItemDate = itemView.findViewById(R.id.listItemDate);
        }
    }
}