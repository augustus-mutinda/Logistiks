package com.paul_karanu.logistiks.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.paul_karanu.logistiks.R;

public class NewFoodRVAdapter extends RecyclerView.Adapter<NewFoodRVAdapter.NewFoodRVViewholder> {

    private String [] foods;
    private Context context;

    public NewFoodRVAdapter(String [] foods, Context context){
        this.foods = foods;
        this.context = context;
    }

    public class NewFoodRVViewholder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        TextView title;

        public NewFoodRVViewholder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.modelFoodCategory);
            title = itemView.findViewById(R.id.modelFoodTV);
        }
    }

    @NonNull
    @Override
    public NewFoodRVViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewFoodRVViewholder(LayoutInflater.from(context).inflate(R.layout.model_food_category, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewFoodRVViewholder newFoodRVViewholder, int i) {
        newFoodRVViewholder.title.setText(foods[i]);
    }

    @Override
    public int getItemCount() {
        return foods.length;
    }
}
