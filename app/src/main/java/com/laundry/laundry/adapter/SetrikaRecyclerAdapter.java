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
import com.laundry.laundry.model.Setrika;
import com.laundry.laundry.ui.setrika.DetailSetrika;

import java.util.ArrayList;
import java.util.List;

public class SetrikaRecyclerAdapter extends RecyclerView.Adapter<SetrikaRecyclerAdapter.adapterSetrikaViewHolder>  implements Filterable {
    private List<Setrika> dataList;
    private List<Setrika> filteredDataList;
    private Context context;
    private View view;

    public SetrikaRecyclerAdapter(Context context, List<Setrika> dataList){
        this.context = context;
        this.dataList = dataList;
        this.filteredDataList = dataList;
    }

    @NonNull
    @Override
    public SetrikaRecyclerAdapter.adapterSetrikaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.recycler_adapter_setrika, parent, false);
        return new SetrikaRecyclerAdapter.adapterSetrikaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetrikaRecyclerAdapter.adapterSetrikaViewHolder holder, int position) {
        final Setrika setrika = filteredDataList.get(position);

        holder.tvIdSetrika.setText(setrika.getStringId());
        holder.tvBerat.setText(String.valueOf(setrika.getBerat()));
        holder.tvJumlah.setText(String.valueOf(setrika.getJumlah_pakaian()));
        holder.tvJenis.setText(setrika.getJenis_pakaian());

        holder.mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                DetailSetrika dialog = new DetailSetrika();
                dialog.show(manager, "dialog");

                Bundle args = new Bundle();
                args.putSerializable("setrika", setrika);
                dialog.setArguments(args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (filteredDataList != null) ? filteredDataList.size() : 0;
    }

    public class adapterSetrikaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIdSetrika, tvBerat, tvJumlah, tvJenis;
        private LinearLayout mParent;

        public adapterSetrikaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdSetrika     = itemView.findViewById(R.id.tvIdSetrika);
            tvBerat         = itemView.findViewById(R.id.tvBeratSetrika);
            tvJumlah        = itemView.findViewById(R.id.tvJumlahSetrika);
            tvJenis         = itemView.findViewById(R.id.tvJenisSetrika);
            mParent         = itemView.findViewById(R.id.setrikalayout);
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
                    List<Setrika> filteredList = new ArrayList<>();
                    for (Setrika Setrika : dataList){
                        if(Setrika.getStringId().toLowerCase().contains(charSequenceString.toLowerCase())){
                            filteredList.add(Setrika);
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
                filteredDataList = (List<Setrika>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
