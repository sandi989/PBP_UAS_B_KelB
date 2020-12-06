package com.laundry.laundry.unittest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddSetrikaPresenterTest {
    @Mock
    private AddSetrikaView view;
    @Mock
    private AddSetrikaService service;
    private AddSetrikaPresenter presenter;
    @Before
    public void setUp() throws Exception {
        presenter = new AddSetrikaPresenter(view, service);
    }

    @Test
    public void shouldShowErrorMessageWhenBeratIsEmpty() throws Exception {
        when(String.valueOf(view.getBerat())).thenReturn("");
        System.out.println("berat : "+String.valueOf(view.getBerat()));
        presenter.onAddClicked();
        verify(view).showBeratError("Berat Tidak Boleh Kosong");
    }

    @Test
    public void shouldShowErrorMessageWhenJumlahIsEmpty() throws Exception {
        when(String.valueOf(view.getJumlah())).thenReturn("");
        System.out.println("jumlah : "+String.valueOf(view.getJumlah()));
        presenter.onAddClicked();
        verify(view).showJumlahError("Jumlah Tidak Boleh Kosong");
    }

    @Test
    public void shouldShowErrorMessageWhenJenisIsEmpty() throws Exception {
        when(view.getJenis()).thenReturn("");
        System.out.println("jenis pakaian: "+view.getJenis());
        presenter.onAddClicked();
        verify(view).showJenisError("Jumlah Tidak Boleh Kosong");
    }

    @Test
    public void shouldStartMainActivityWhenAllCorrect() throws
            Exception {
        when(String.valueOf(view.getBerat())).thenReturn("10");
        System.out.println("berat : "+String.valueOf(view.getBerat()));
        when(String.valueOf(view.getJumlah())).thenReturn("10");
        System.out.println("jumlah: "+String.valueOf(view.getJumlah()));
        when(view.getJenis()).thenReturn("Biasa");
        System.out.println("jenis pakaian: "+view.getJenis());
        System.out.println("Hasil : "+service.getValid(view, view.getBerat(),
                view.getJumlah(), view.getJenis()));
        presenter.onAddClicked();
        //verify(view).startMainActivity();
    }


}