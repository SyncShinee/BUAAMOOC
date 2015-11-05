package cn.edu.buaamooc.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.activity.MoocMainActivity;
import cn.edu.buaamooc.tools.Login;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private View layout;
    private MoocMainActivity activity;


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
        int tabIndex = getArguments().getInt("tabIndex");
        String nameArgu = getArguments().getString("username");
        ((TextView) layout.findViewById(R.id.usernameText)).setText(nameArgu);
        TextView loginButton = (TextView) layout.findViewById(R.id.button_fragment_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment.this.login();
            }
        });
        return layout;
    }

    @Override
    public void onDestroyView(){
        Log.e("LoginFragment", "onDestroyView.");
        super.onDestroyView();
    }

    /**
     * Get information from controls and call Login.login()
     */
    public void login(){
        String username = ((TextView) layout.findViewById(R.id.usernameText)).getText().toString();
        String password = ((TextView) layout.findViewById(R.id.passwordText)).getText().toString();
        Login login = new Login(username,password).setContext(activity).setAuto(false);
        CheckBox rememberMe = (CheckBox) layout.findViewById(R.id.checkbox_login_rememberMe);
        if (rememberMe.isChecked()) {
            login.setRememberMe();
        }
        activity.setUsername(username);
        login.login();
    }
}
