package com.sovereign.budgetmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sovereign.budgetmanager.Database.LimitDatabaseHelper;
import com.sovereign.budgetmanager.Database.LimitModel;
import com.sovereign.budgetmanager.Database.TransactionDatabaseHelper;
import com.sovereign.budgetmanager.Database.TransactionModel;

import java.util.List;

public class LimitItemAdapter extends RecyclerView.Adapter<LimitItemAdapter.ViewHolder> {
    private Context mContext;
    private List<LimitModel> limitModelList;

    LimitDatabaseHelper limitDatabaseHelper;
    TransactionDatabaseHelper transactionDatabaseHelper;

    public LimitItemAdapter(Context mContext) {
        this.mContext = mContext;


        limitDatabaseHelper = new LimitDatabaseHelper(mContext);
        limitModelList = limitDatabaseHelper.getAll();
    }


    @NonNull
    @Override
    public LimitItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.single_limit_item, parent, false);
        return new LimitItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LimitItemAdapter.ViewHolder holder, int position) {
        LimitModel limitModel = limitModelList.get(position);

        holder.limitItemLimit.setText(String.valueOf(limitModel.getLimit()));
        holder.limitItemSpent.setText(String.valueOf(getSpent(limitModel.getCat())));
        holder.limitItemAvail.setText(String.valueOf(getAvail(limitModel)));

        String[] cats = mContext.getResources().getStringArray(R.array.ExpenseCats);
        holder.limitItemCat.setText(cats[limitModel.getCat()]);
    }

    @Override
    public int getItemCount() {
        return limitModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView limitItemLimit;
        public TextView limitItemCat;
        public TextView limitItemAvail;
        public TextView limitItemSpent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            limitItemLimit = itemView.findViewById(R.id.limit_Item_Limit);
            limitItemCat = itemView.findViewById(R.id.listItemCat);
            limitItemAvail = itemView.findViewById(R.id.limit_Item_avail);
            limitItemSpent = itemView.findViewById(R.id.limit_Item_Spent);
        }
    }

    private int getSpent(int cat){
        List<TransactionModel> transactionModelList = transactionDatabaseHelper.getAll();
        String[] expenseCats = mContext.getResources().getStringArray(R.array.ExpenseCats);
        int spentSum = 0;

        for (TransactionModel t: transactionModelList){
            if (t.getAmount()!=0){
                if (!(t.isCredit())){
                    if (t.getCat() == cat){
                        spentSum += t.getAmount();
                    }
                }
            }
        }
        return spentSum;
    }

    private int getAvail(LimitModel limitModel){
        int spent = getSpent(limitModel.getCat());
        int budget = 0;

        if (limitModel.getType() == 0){
            budget = limitModel.getLimit()-spent;
        } else {
            budget = (getIncome()*limitModel.getLimit())/100 - spent;
        }
        return budget;
    }

    private int getIncome() {
        List<TransactionModel> transactionModelList = transactionDatabaseHelper.getAll();
        int catCreditSum = 0;
        for (TransactionModel t: transactionModelList){
            if (t.isCredit()) {
                catCreditSum += t.getAmount();
            }
        }
        return catCreditSum;
    }
}
