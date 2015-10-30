package cn.edu.buaamooc.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.MoocMainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private View layout;
    private MoocMainActivity activity;
    private int tabIndex;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("LoginFragment", "onCreateView.");
        layout = inflater.inflate(R.layout.fragment_login, container, false);
        activity = (MoocMainActivity) getActivity();
        tabIndex = getArguments().getInt("tabIndex");
        return layout;
    }

    @Override
    public void onDestroyView(){
        Log.e("LoginFragment", "onDestroyView.");
        super.onDestroyView();
    }


}
