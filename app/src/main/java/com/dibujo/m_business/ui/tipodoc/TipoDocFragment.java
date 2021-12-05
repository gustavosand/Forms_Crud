package com.dibujo.m_business.ui.tipodoc;

        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.lifecycle.Observer;
        import androidx.lifecycle.ViewModelProvider;

        import com.dibujo.m_business.R;
        import com.dibujo.m_business.databinding.FragmentTipodocBinding;

public class TipoDocFragment extends Fragment {

    private TipoDocViewModel tipodocViewModel;
    private FragmentTipodocBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tipodocViewModel =
                new ViewModelProvider(this).get(TipoDocViewModel.class);

        binding = FragmentTipodocBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textTipodoc;
        tipodocViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}