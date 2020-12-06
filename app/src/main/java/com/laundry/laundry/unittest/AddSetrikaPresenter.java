package com.laundry.laundry.unittest;

import com.laundry.laundry.model.Setrika;

public class AddSetrikaPresenter {
    private AddSetrikaView view;
    private AddSetrikaService service;
    private AddSetrikaCallback callback;
    public AddSetrikaPresenter(AddSetrikaView view, AddSetrikaService service) {
        this.view = view;
        this.service = service;
    }
    public void onAddClicked() {
        if (String.valueOf(view.getBerat()).isEmpty()) {
            view.showBeratError("Berat tidak boleh kosong");
            return;
        }else if (String.valueOf(view.getJumlah()).isEmpty()) {
            view.showJumlahError("Jumlah tidak boleh kosong");
            return;
        }else if (view.getJenis().isEmpty()){
            view.showJenisError("Jenis tidak boleh kosong");
            return;
        } else{
            service.addsetrika(view, view.getBerat(), view.getJumlah(), view.getJenis(), new
                    AddSetrikaCallback() {
                        @Override
                        public void onSuccess(boolean value, Setrika setrika) {
                            view.startMainActivity();
                        }

                        @Override
                        public void onError() {
                        }
                    });
            return;
        }
    }
}
