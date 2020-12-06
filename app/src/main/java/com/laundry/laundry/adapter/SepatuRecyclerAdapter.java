package com.laundry.laundry.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laundry.laundry.R;
import com.laundry.laundry.model.Sepatu;
import com.laundry.laundry.ui.sepatu.DetailSepatu;

import java.util.ArrayList;
import java.util.List;

public class SepatuRecyclerAdapter extends RecyclerView.Adapter<SepatuRecyclerAdapter.adapterSepatuViewHolder> implements Filterable {
    private List<Sepatu> dataList;
    private List<Sepatu> filteredDataList;
    private Context context;
    private View view;

    public SepatuRecyclerAdapter(Context context, List<Sepatu> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.filteredDataList = dataList;
    }

    @NonNull
    @Override
    public SepatuRecyclerAdapter.adapterSepatuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.recycler_adapter_sepatu, parent, false);
        return new SepatuRecyclerAdapter.adapterSepatuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SepatuRecyclerAdapter.adapterSepatuViewHolder holder, int position) {
        final Sepatu sepatu = filteredDataList.get(position);

        holder.tvIdSepatu.setText(sepatu.getStringId());
        holder.tvJenisLayanan.setText(sepatu.getJenis_layanan());
        holder.tvKondisi.setText(sepatu.getKondisi());
        holder.tvJenisSepatu.setText(sepatu.getJenis_sepatu());

        holder.mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                DetailSepatu dialog = new DetailSepatu();
                dialog.show(manager, "dialog");

                Bundle args = new Bundle();
                args.putSerializable("sepatu", sepatu);
                dialog.setArguments(args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (filteredDataList != null) ? filteredDataList.size() : 0;
    }

    public class adapterSepatuViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIdSepatu, tvJenisLayanan, tvKondisi, tvJenisSepatu;
        private LinearLayout mParent;

        public adapterSepatuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdSepatu      = itemView.findViewById(R.id.tvIdSepatu);
            tvJenisLayanan  = itemView.findViewById(R.id.tvLayananSepatu);
            tvKondisi       = itemView.findViewById(R.id.tvKondisiSepatu);
            tvJenisSepatu   = itemView.findViewById(R.id.tvJenisSepatu);
            mParent         = itemView.findViewById(R.id.sepatulayout);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if(charSequenceString.isEmpty()){
                    filteredDataList = dataList;
                } else {
                    List<Sepatu> filteredList = new ArrayList<>();
                    for (Sepatu Sepatu : dataList){
                        if(Sepatu.getStringId().toLowerCase().contains(charSequenceString.toLowerCase())){
                            filteredList.add(Sepatu);
                        }
                        filteredDataList = filteredList;
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredDataList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredDataList = (List<Sepatu>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
