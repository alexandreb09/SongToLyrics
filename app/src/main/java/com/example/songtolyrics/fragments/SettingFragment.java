package com.example.songtolyrics.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.model.ServerConfig;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;


public class SettingFragment extends BaseFragment{

    private View mParentView;
    private EditText mServerIP;
    private EditText mServerPort;
    private CheckBox mIsHTTPS;
    private TextView mAdress;
    private ProgressBar mLoading;
    private Button mButtonTest;

    // Required empty public constructor
    public SettingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParentView = inflater.inflate(R.layout.fragment_setting, container, false);

        // Set toolbar title
        String toolBarTitle = mContext.getResources().getString(R.string.label_setting);
        Utils.setToolbarTitle(getActivity(), toolBarTitle);


        mServerIP   = mParentView.findViewById(R.id.setting_server_path);
        mServerPort = mParentView.findViewById(R.id.setting_server_port);
        mIsHTTPS    = mParentView.findViewById(R.id.setting_is_https);
        mButtonTest = mParentView.findViewById(R.id.setting_query_btn);
        mLoading    = mParentView.findViewById(R.id.setting_test_progress_bar);
        mAdress     = mParentView.findViewById(R.id.setting_url_result);

        // Set default values
        ServerConfig serverConfig = Utils.getPythonServerParams(mContext);
        mServerIP.setText(serverConfig.getIP());
        mServerPort.setText(serverConfig.getPort());
        mIsHTTPS.setChecked(serverConfig.isHTTPS());

        addOnTextChangeListeners();

        mButtonTest.setOnClickListener((v) -> {
            mLoading.setVisibility(View.VISIBLE);
            mButtonTest.setEnabled(false);

            ServerConfig current_server_config = new ServerConfig(mServerIP.getText().toString(),
                    mServerPort.getText().toString(),
                    mIsHTTPS.isChecked());
            // Run Test connexion
            new TestConnexionTask(getActivity(), current_server_config)
                    .execute();
        });

        // Show URL
        updateURL();

        return mParentView;
    }


    private void updateURL(){
        String url = "$1://$2:$3"
                .replace("$1", mIsHTTPS.isChecked() ? "https" : "http")
                .replace("$3", mServerPort.getText())
                .replace("$2", mServerIP.getText());
        mAdress.setText(url);
    }


    private void addOnTextChangeListeners(){
        mServerPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updateURL();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mServerIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updateURL();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mIsHTTPS.setOnCheckedChangeListener((buttonView, isChecked) -> updateURL());
    }



    static class TestConnexionTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<FragmentActivity> activityReference;
        ServerConfig serverConfig;

        TestConnexionTask(FragmentActivity fragmentActivity, ServerConfig serverConfig){
            activityReference = new WeakReference<>(fragmentActivity);
            this.serverConfig = serverConfig;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                // create URL
                URL myUrl = new URL(serverConfig.getUrl());

                // Test connexion
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(1500);
                connection.connect();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success){
            // get a reference to the activity if it is still there
            FragmentActivity activity = activityReference.get();

            // Check the current activity is still running
            if (!(activity == null || activity.isFinishing())){
                Context context = activity.getApplicationContext();

                View view               = activity.findViewById(R.id.fragment_setting);
                ProgressBar loading     = view.findViewById(R.id.setting_test_progress_bar);
                Button buttonTest       = view.findViewById(R.id.setting_query_btn);

                loading.setVisibility(View.INVISIBLE);
                buttonTest.setEnabled(true);

                if (success){
                    Utils.storePythonServerParams(context, serverConfig);

                    new FancyAlertDialog.Builder(activity)
                        .setTitle(context.getResources().getString(R.string.settings_server_connexion_success))
                        .setBackgroundColor(Color.parseColor("#F57C00"))
                        .setMessage(context.getResources().getString(R.string.settings_server_connexion_success_msg))
                        .setNegativeBtnText("Annuler")
                        .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                        .setPositiveBtnBackground(Color.parseColor("#F57C00"))
                        .setPositiveBtnText("Valider !")
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_done_black_24dp, Icon.Visible)
                        .OnPositiveClicked(() -> Navigation.findNavController(view).popBackStack())
                        .OnNegativeClicked(() -> {})
                        .build();
                }else{
                    new FancyAlertDialog.Builder(activity)
                        .setTitle(context.getResources().getString(R.string.settings_server_connexion_fail))
                        .setBackgroundColor(Color.parseColor("#F57C00"))
                        .setMessage(context.getResources().getString(R.string.settings_server_connexion_fail_msg))
                        .setNegativeBtnText("Quitter")
                        .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                        .setPositiveBtnBackground(Color.parseColor("#F57C00"))
                        .setPositiveBtnText("Modifier !")
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_pan_tool_black_24dp, Icon.Visible)
                        .OnPositiveClicked(() -> {})
                        .OnNegativeClicked(() -> {
                            SettingFragmentDirections.ActionSettingFragmentToHomeFragment action =
                                    SettingFragmentDirections.actionSettingFragmentToHomeFragment("","");
//                            Navigation.findNavController(view).popBackStack(R.id.reccordFragment, true);
                            Navigation.findNavController(view).navigate(action);
                        })
                        .build();

                }
            }
        }
    }
}
