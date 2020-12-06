package com.laundry.laundry.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.laundry.laundry.R;
import com.laundry.laundry.ui.home.UpdateFragment;
import com.laundry.laundry.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.UserViewHolder>{
    private Context context;
    private Order order;
    private List<Order> orderList;
    private List<Order> userisFull = new ArrayList<>();

    public OrderRecyclerViewAdapter(Context context, List<Order> list) {
        this.context = context;
        this.orderList = list;
        userisFull.addAll(orderList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderRecyclerViewAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerViewAdapter.UserViewHolder holder, int position) {
        order = orderList.get(position);
        holder.viewId.setText(order.getStringId());
        holder.viewJP.setText(order.getStringJumlah_pakaian()+" buah");
        holder.viewBerat.setText(order.getStringBerat()+" kg");
        holder.viewLayanan.setText(order.getLayanan());
        holder.viewTgl_Masuk.setText(order.getTanggal_masuk());
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    public Filter getFilter() { return filterOrder; }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView viewId, viewJP, viewBerat, viewLayanan, viewTgl_Masuk;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            viewId = itemView.findViewById(R.id.id);
            viewJP = itemView.findViewById(R.id.jumlah_pakaian);
            viewBerat = itemView.findViewById(R.id.berat);
            viewLayanan = itemView.findViewById(R.id.layanan);
            viewTgl_Masuk = itemView.findViewById(R.id.tanggal_masuk);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            Bundle data = new Bundle();
            Order order = orderList.get(getAdapterPosition());
            data.putSerializable("order", order);
            UpdateFragment updateFragment = new UpdateFragment();
            updateFragment.setArguments(data);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, updateFragment)
                    .commit();
        }
    }

    //Menmfilter order berdasarkan id yang dimasukkan
    private Filter filterOrder = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Order> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userisFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Order order : userisFull) {
                    if (order.getStringId().toLowerCase().contains(filterPattern)){
                        filteredList.add(order);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        //Menampilkan daftar order dengan id order yang dimasukkan
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            orderList.clear();

            orderList.addAll((List<Order>) results.values);
            notifyDataSetChanged();
        }
    };
}
