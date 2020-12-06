package com.laundry.laundry.ui.setrika;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laundry.laundry.R;
import com.laundry.laundry.adapter.SetrikaRecyclerAdapter;

import com.laundry.laundry.api.SetrikaAPI;
import com.laundry.laundry.model.Setrika;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.Request.Method.GET;

public class SetrikaFragment extends Fragment {
    private RecyclerView recyclerView;
    private SetrikaRecyclerAdapter adapter;
    private List<Setrika> listSetrika;
    private View view;

    private SearchView searchView;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setrika, container, false);

        searchView = view.findViewById(R.id.search_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        fabAdd = view.findViewById(R.id.fab);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new AddSetrika()).addToBackStack(null).commit();
            }
        });

        swipeRefresh.setRefreshing(true);

        loadDataSetrika();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataSetrika();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void loadDataSetrika() {
        setAdapter();
        getSetrika();
    }

    private void getSetrika() {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());

        final JsonObjectRequest stringRequest = new JsonObjectRequest(GET, SetrikaAPI.URL_SELECT
                , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    if(!listSetrika.isEmpty())
                        listSetrika.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        int id                  = jsonObject.optInt("id");
                        Double berat            = jsonObject.optDouble("berat");
                        int jumlah_pakaian      = jsonObject.optInt("jumlah_pakaian");
                        String jenis_pakaian    = jsonObject.optString("jenis_pakaian");

                        Setrika setrika = new Setrika(id, berat, jumlah_pakaian, jenis_pakaian);

                        listSetrika.add(setrika);
                    }
                    adapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Toast.makeText(view.getContext(), response.optString("message"),
                        Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void setAdapter() {
        getActivity().setTitle("Data Setrika");

        listSetrika = new ArrayList<Setrika>();
        recyclerView = view.findViewById(R.id.setrika_rv);
        adapter = new SetrikaRecyclerAdapter(view.getContext(), listSetrika);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                adapter.getFilter().filter(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryString) {
                adapter.getFilter().filter(queryString);
                return false;
            }
        });
    }
}