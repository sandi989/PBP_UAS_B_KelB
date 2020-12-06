package com.laundry.laundry.ui.setrika;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.util.Log;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.laundry.laundry.R;
import com.laundry.laundry.adapter.SetrikaRecyclerAdapter;

import com.laundry.laundry.api.SetrikaAPI;
import com.laundry.laundry.model.Setrika;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private MaterialButton btnCetak;

    private AlertDialog.Builder builder;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private static final String TAG = "PdfCreatorActivity";
    private File pdfFile;
    private PdfWriter writer;

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

        btnCetak = view.findViewById(R.id.btnDownload);
        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(getContext());

                builder.setCancelable(false);
                builder.setMessage("Apakah anda yakin ingin mencetak surat pemesanan ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            createPdfWrapper();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.WHITE);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.WHITE);
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

    private void createPdfWrapper() throws FileNotFoundException, DocumentException{
        int hasWriteStoragePermission = 0;
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("Izinkan aplikasi untuk akses penyimpanan?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new
                                                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            createPdf();
        }
    }

    private void createPdf() throws FileNotFoundException, DocumentException{
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download/");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Direktori baru untuk file pdf berhasil dibuat");
        }
        String pdfname = "LaporanTransaksiSetrika"+".pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
        writer = PdfWriter.getInstance(document, output);
        document.open();
        Paragraph judul = new Paragraph(" LAPORAN TRANSAKSI ORDER SETRIKA \n\n", new
                com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16,
                com.itextpdf.text.Font.BOLD, BaseColor.BLACK));
        judul.setAlignment(Element.ALIGN_CENTER);
        document.add(judul);
        PdfPTable tables = new PdfPTable(new float[]{16, 8});
        tables.getDefaultCell().setFixedHeight(50);
        tables.setTotalWidth(PageSize.A4.getWidth());
        tables.setWidthPercentage(100);
        tables.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        PdfPCell cellSupplier = new PdfPCell();
        cellSupplier.setPaddingLeft(20);
        cellSupplier.setPaddingBottom(10);
        cellSupplier.setBorder(Rectangle.NO_BORDER);
        Paragraph Kepada= new Paragraph(
                "Kepada Yth : \n" + "Pemilik Cupang Laundry"+"\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                        com.itextpdf.text.Font.NORMAL, BaseColor.BLACK)
        );
        cellSupplier.addElement(Kepada);
        tables.addCell(cellSupplier);
        PdfPCell cellPO = new PdfPCell();
        Paragraph NomorTanggal = new Paragraph(
                "No : " + "CL/STR/1" + "\n\n" +
                        "Tanggal : " + "Rabu, 9 Desember 2020" + "\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                        com.itextpdf.text.Font.NORMAL, BaseColor.BLACK)
        );
        NomorTanggal.setPaddingTop(5);
        tables.addCell(NomorTanggal);
        document.add(tables);
        com.itextpdf.text.Font f = new
                com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);
        Paragraph Pembuka = new Paragraph("\nBerikut merupakan laporan transaksi pada order setrika: \n\n",f);
        Pembuka.setIndentationLeft(20);
        document.add(Pembuka);
        PdfPTable tableHeader = new PdfPTable(new float[]{3,3,3});
        tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeader.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        tableHeader.getDefaultCell().setFixedHeight(30);
        tableHeader.setTotalWidth(PageSize.A4.getWidth());
        tableHeader.setWidthPercentage(100);
        PdfPCell h1 = new PdfPCell(new Phrase("Berat Total"));
        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
        h1.setPaddingBottom(5);
        PdfPCell h2 = new PdfPCell(new Phrase("Jumlah Pakaian"));
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        h2.setPaddingBottom(5);
        PdfPCell h4 = new PdfPCell(new Phrase("Jenis Pakaian"));
        h4.setHorizontalAlignment(Element.ALIGN_CENTER);
        h4.setPaddingBottom(5);
        tableHeader.addCell(h1);
        tableHeader.addCell(h2);
        tableHeader.addCell(h4);
        PdfPCell[] cells = tableHeader.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        document.add(tableHeader);
        PdfPTable tableData = new PdfPTable(new float[]{3,3,3});
        tableData.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableData.getDefaultCell().setFixedHeight(30);
        tableData.setTotalWidth(PageSize.A4.getWidth());
        tableData.setWidthPercentage(100);
        tableData.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        int arrLength = listSetrika.size();
        for(int x=0;x<arrLength;x++){
            for(int i=0;i<cells.length;i++){
                if(i==0){
                    tableData.addCell(String.valueOf(listSetrika.get(x).getBerat()));
                }else if(i==1){
                    tableData.addCell(String.valueOf(listSetrika.get(x).getJumlah_pakaian()));
                }else{
                    tableData.addCell(listSetrika.get(x).getJenis_pakaian());
                }
            }
        }
        document.add(tableData);

        com.itextpdf.text.Font h = new
                com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                com.itextpdf.text.Font.NORMAL);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        String tglDicetak = sdf.format(currentTime);
        Paragraph P = new Paragraph("\nDicetak tanggal " + tglDicetak, h);
        P.setAlignment(Element.ALIGN_RIGHT);
        document.add(P);
        document.close();
        previewPdf();
    }

    private void previewPdf() {
        PackageManager packageManager = getContext().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                uri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName()+".provider",
                        pdfFile);
            } else {
                uri = Uri.fromFile(pdfFile);
            }
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(uri, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            getContext().grantUriPermission("com.laundry.laundry.ui.setrika", uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(pdfIntent);
        } else {
            Toast.makeText(getContext(),"Unduh pembuka PDF untuk menampilkan file ini",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}