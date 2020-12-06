package com.laundry.laundry.unittest;

import com.laundry.laundry.model.Setrika;

public interface AddSetrikaCallback {
    void onSuccess(boolean value, Setrika setrika);
    void onError();
}
