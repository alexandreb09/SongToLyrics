package com.example.songtolyrics.fragments;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.ServerConfig;
import com.google.gson.Gson;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static com.example.songtolyrics.Parameters.API_PYTHON_METHOD;
import static com.example.songtolyrics.Parameters.API_PYTHON_METHOD_TEST_CONNEXION;
import static com.example.songtolyrics.Parameters.API_PYTHON_PORT;
import static com.example.songtolyrics.Parameters.API_PYTHON_URL;
import static com.example.songtolyrics.Parameters.RECCORD_TIME;


public class RecordFragment extends BaseFragment implements View.OnClickListener{
    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private static final String LOG_TAG = "AudioRecordTest";
    private static String fileName = null;

    private MediaRecorder   recorder = null;
    private MediaPlayer     player = null;

    private long timeCountInMilliSeconds = RECCORD_TIME * 1000;
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private View mParentView;
    private ProgressBar progressBarRecord;
    private ProgressBar progressBarRecognition;
    private ProgressBar recordButtonLoading;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;
    private Button recordButton;


    // Required empty public constructor
    public RecordFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check connexion
        new TestConnexionTask(getActivity()).execute();

        // Set toolbar title
        String toolBarTitle = mContext.getResources().getString(R.string.menu_record);
        Utils.setToolbarTitle(getActivity(), toolBarTitle);


        // Inflate the layout for this fragment
        mParentView = inflater.inflate(R.layout.fragment_reccord, container, false);

        fileName = Utils.getFileName(mContext);

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();

        return mParentView;
    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        progressBarRecord       = mParentView.findViewById(R.id.record_progress_bar_record);
        progressBarRecognition  = mParentView.findViewById(R.id.record_progress_bar_recognition);
        textViewTime            = mParentView.findViewById(R.id.record_time_txt);
        imageViewReset          = mParentView.findViewById(R.id.record_reset);
        imageViewStartStop      = mParentView.findViewById(R.id.record_start_stop);
        recordButton            = mParentView.findViewById(R.id.record_record_btn);
        recordButtonLoading     = mParentView.findViewById(R.id.record_btn_progress_bar);
    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        imageViewReset      .setOnClickListener(this);
        imageViewStartStop  .setOnClickListener(this);
        recordButton        .setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     * @param view: View
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_reset:
                reset();
                break;
            case R.id.record_start_stop:
            case R.id.record_record_btn:
                startStop();
                break;
        }
    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        onRecord(false);
        stopCountDownTimer();
        onRecord(true);
        startCountDownTimer();
    }


    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            recordButton.setText(R.string.record_in_progress);
            recordButton.setEnabled(false);
            recordButtonLoading.setVisibility(View.VISIBLE);
            onRecord(true);

            progressBarRecord.setProgress(100);                                                     // Reset progress value
            imageViewReset.setVisibility(View.VISIBLE);                                             // showing the reset icon
            imageViewStartStop.setImageResource(R.drawable.ic_pause);                               // changing play icon to stop icon
            timerStatus = TimerStatus.STARTED;                                                      // changing the timer status to started
            startCountDownTimer();                                                                  // call to start the count down timer
        }else {
            onRecord(false);
            imageViewReset.setVisibility(View.GONE);                                                // hiding the reset icon
            imageViewStartStop.setImageResource(R.drawable.ic_stop);                                // changing stop icon to start icon
            timerStatus = TimerStatus.STOPPED;                                                      // changing the timer status to stopped
            stopCountDownTimer();
        }
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {
        long total_time = timeCountInMilliSeconds + 1000;
        countDownTimer = new CountDownTimer(total_time, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                // Compute % progress
                int progress = (int) Math.round(millisUntilFinished * 100. / total_time);
                progressBarRecord.setProgress(progress);
            }

            @Override
            public void onFinish() {
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));                    // Reset timer text
                progressBarRecord.setProgress(100);                                                 // Reset progress value
                imageViewReset.setVisibility(View.GONE);                                            // hiding the reset icon
                imageViewStartStop.setImageResource(R.drawable.ic_play_button);                     // changing stop icon to start icon
                timerStatus = TimerStatus.STOPPED;                                                  // changing the timer status to stopped

                // Stop reccord
                onRecord(false);

                // Start PYTHON recognition
                imageViewStartStop.setVisibility(View.GONE);
                progressBarRecord.setVisibility(View.INVISIBLE);
                progressBarRecognition.setVisibility(View.VISIBLE);
                recordButton.setText(R.string.record_recognition_in_progress);
                RetrieveFeedTask runningTask = new RetrieveFeedTask(getActivity());
                runningTask.execute();
            }
        }.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }


    /**
     * Convert millisecond to string
     * @param milliSeconds: int
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {
        return (int) (milliSeconds / 1000.)+ " s";
    }


    //============================================//
    //                  RECCORD                   //
    //============================================//
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


    static class TestConnexionTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<FragmentActivity> activityReference;

        TestConnexionTask(FragmentActivity fragmentActivity){
            activityReference = new WeakReference<>(fragmentActivity);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                ServerConfig serverConfig = Utils.getPythonServerParams(activityReference.get().getApplicationContext());
                String url = serverConfig.getUrl(API_PYTHON_METHOD_TEST_CONNEXION);

                URL myUrl = new URL(url);
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(1000);
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
                View view = activity.findViewById(R.id.fragment_reccord);
                Context context = activity.getApplicationContext();

                if (success){
                    TextView connexionTest = view.findViewById(R.id.record_connexion_server_txt);
                    ImageView imageViewStart = view.findViewById(R.id.record_start_stop);
                    ImageView timeTxt = view.findViewById(R.id.record_time_txt);
                    Button recordButton = view.findViewById(R.id.record_record_btn);
                    ProgressBar progressBar1 = view.findViewById(R.id.record_progress_bar_record);
                    ProgressBar progressBar2 = view.findViewById(R.id.record_progress_bar_recognition);

                    progressBar1.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.INVISIBLE);
                    connexionTest.setVisibility(View.GONE);
                    imageViewStart.setVisibility(View.VISIBLE);
                    timeTxt.setVisibility(View.VISIBLE);
                    recordButton.setEnabled(true);

                }else{

                    new FancyAlertDialog.Builder(activity)
                        .setTitle(context.getResources().getString(R.string.record_server_unavailable_title))
                        .setBackgroundColor(Color.parseColor("#F57C00"))
                        .setMessage(context.getResources().getString(R.string.record_server_unavailable_message))
                        .setNegativeBtnText("QUITTER")
                        .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                        .setPositiveBtnBackground(Color.parseColor("#F57C00"))
                        .setPositiveBtnText("EDITER")
                        .setAnimation(Animation.POP)
                        .setIcon(R.drawable.ic_pan_tool_black_24dp, Icon.Visible)
                        .OnPositiveClicked(() -> {
                            NavDirections action = RecordFragmentDirections.actionReccordFragmentToSettingFragment();
                            Navigation.findNavController(view).navigate(action);
                        })
                        .OnNegativeClicked(() -> Navigation.findNavController(view).popBackStack(R.id.HomeFragment, true))
                        .build();
                }
            }
        }
    }


    static class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private WeakReference<FragmentActivity> activityReference;

        RetrieveFeedTask(FragmentActivity context){
            activityReference = new WeakReference<>(context);
        }

        protected String doInBackground(Void... urls) {
            URL url;
            try {
                url = new URL(API_PYTHON_URL + ":" + API_PYTHON_PORT + "/" + API_PYTHON_METHOD);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "";
            }

            return tryUpload(url, fileName);
        }

        private String tryUpload(URL url, String filepath){
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;

            byte[] buffer;
            int bytesRead, bytesAvailable, bufferSize;

            int maxBufferSize = 1024 * 1024;
            File selectedFile = new File(filepath);
            String lineEnd = "\r\n", twoHyphens = "--", boundary = "*****";

            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);                                                        // Allow Inputs
                connection.setDoOutput(true);                                                       // Allow Outputs
                connection.setUseCaches(false);                                                     // Don't use a cached Copy
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", filepath);

                // creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                // writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + filepath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                // returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                // selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                // setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                // reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                // loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    // write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

                return readRequestResponse(connection);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("ERROR","File Not Found");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("ERROR","URL error!" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR","Cannot Read/Write File!");
            }

            return new Music().toString();
        }


        private String readRequestResponse(HttpURLConnection connection){
            String line;
            try {
                if (connection.getResponseCode() < 400){
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();

                    return stringBuilder.toString();
                }
            }
            catch (java.io.IOException ex) {
                Log.e("Error", ex.toString());
            }
            finally{
                connection.disconnect();
            }

            return new Music().toString();
        }

        protected void onPostExecute(String response) {
            // get a reference to the activity if it is still there
            FragmentActivity activity = activityReference.get();

            // Check the current activity is still running
            if (!(activity == null || activity.isFinishing())){
                // Find elements
                View view                           = activity.findViewById(R.id.fragment_reccord);
                Button recordButton                 = activity.findViewById(R.id.record_record_btn);
                TextView timerText                  = activity.findViewById(R.id.record_time_txt);
                ProgressBar progressBarRecord       = activity.findViewById(R.id.record_progress_bar_record);
                ProgressBar recordBtnProgress       = activity.findViewById(R.id.record_btn_progress_bar);
                ProgressBar progressBarRecognition  = activity.findViewById(R.id.record_progress_bar_recognition);
                ImageView startStop                 = activity.findViewById(R.id.record_start_stop);

                timerText               .setVisibility(View.VISIBLE);
                progressBarRecord       .setVisibility(View.VISIBLE);
                startStop               .setVisibility(View.VISIBLE);
                recordBtnProgress       .setVisibility(View.INVISIBLE);
                progressBarRecognition  .setVisibility(View.INVISIBLE);

                recordButton.setText(R.string.txt_reccord);
                recordButton.setEnabled(true);


                // Check response content
                if(response == null) {
                    showNotFoundDialog(activity, view);
                }
                else {
                    Music music = new Gson().fromJson(response, Music.class);

                    if (music.getTitle().equals("")){
                        showNotFoundDialog(activity, view);
                    }else{
                        // Run Lyrics search activity
                        RecordFragmentDirections.ActionReccordFragmentToHomeFragment action =
                                RecordFragmentDirections.actionReccordFragmentToHomeFragment(music.getArtist(), music.getTitle());
                        Navigation.findNavController(view).navigate(action);
                    }
                }
            }
        }

        private void showNotFoundDialog(FragmentActivity activity, View view){
            Context context = activity.getApplicationContext();
            new FancyAlertDialog.Builder(activity)
                    .setTitle(context.getResources().getString(R.string.record_recognition_failed_title))
                    .setBackgroundColor(Color.parseColor("#F57C00"))
                    .setMessage(context.getResources().getString(R.string.record_recognition_failed_detail))
                    .setNegativeBtnText("QUITTER")
                    .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                    .setPositiveBtnBackground(Color.parseColor("#F57C00"))
                    .setPositiveBtnText("RESSAYER")
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_pan_tool_black_24dp, Icon.Visible)
                    .OnPositiveClicked(() -> {
                    })
                    .OnNegativeClicked(() -> {
                        RecordFragmentDirections.ActionReccordFragmentToHomeFragment action =
                                RecordFragmentDirections.actionReccordFragmentToHomeFragment("","");
                        Navigation.findNavController(view).navigate(action);
                    })
                    .build();
        }
    }
}
