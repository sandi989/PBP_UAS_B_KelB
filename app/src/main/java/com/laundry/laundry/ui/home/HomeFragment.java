package com.laundry.laundry.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laundry.laundry.R;
import com.laundry.laundry.adapter.OrderRecyclerViewAdapter;
import com.laundry.laundry.database.DatabaseOrder;
import com.laundry.laundry.model.Order;

import java.util.List;

public class HomeFragment extends Fragment {

    private SearchView find;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private OrderRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.order_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshLayout = root.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders();
                refreshLayout.setRefreshing(false);
            }
        });
        addOrders(root);
        getOrders();
        addOrders(root);
        search(root);

        return root;
    }

    //Method untuk menuju AddFragment, untuk memasukkan order baru
    private void addOrders(View view) {
        fabAdd = view.findViewById(R.id.fab);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out // popExit
                        );
                fragmentTransaction.replace(R.id.frame_layout, new AddFragment()).addToBackStack(null).commit();
            }
        });
    }

    //Method untuk mendapatkan seluruh daftar order
    private void getOrders() {
        class GetOrders extends AsyncTask<Void, Void, List<Order>> {

            @Override
            protected List<Order> doInBackground(Void... voids) {
                List<Order> orderList = DatabaseOrder
                        .getInstance(getActivity().getApplicationContext())
                        .getDatabase()
                        .orderDao()
                        .getAll();
                return orderList;
            }

            @Override
            protected void onPostExecute(List<Order> orders) {
                super.onPostExecute(orders);
                adapter = new OrderRecyclerViewAdapter(getActivity().getApplicationContext(), orders);
                recyclerView.setAdapter(adapter);
                if (orders.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Empty List", Toast.LENGTH_SHORT).show();
                }
            }
        }
        GetOrders get = new GetOrders();
        get.execute();
    }

    //Method untuk melakukan pencarian
    private void search(View root) {
        find = root.findViewById(R.id.search_view);
        find.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }
}