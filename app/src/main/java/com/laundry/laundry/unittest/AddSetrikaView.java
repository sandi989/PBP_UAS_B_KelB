package com.laundry.laundry.unittest;

import android.content.Context;
import android.view.View;

import com.laundry.laundry.R;
import com.laundry.laundry.model.Sepatu;
import com.laundry.laundry.model.Setrika;

public interface AddSetrikaView {
    int getJumlah();

    void showJumlahError(String message);

    double getBerat();

    void showBeratError(String message);

    String getJenis();

    void showJenisError(String message);

    void startMainActivity();

    void showAddSetrikaError(String message);

    void showErrorResponse(String message);
}
