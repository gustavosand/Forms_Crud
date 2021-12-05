package com.dibujo.m_business.ui.tipodoc;

        import androidx.lifecycle.LiveData;
        import androidx.lifecycle.MutableLiveData;
        import androidx.lifecycle.ViewModel;

public class TipoDocViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TipoDocViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}