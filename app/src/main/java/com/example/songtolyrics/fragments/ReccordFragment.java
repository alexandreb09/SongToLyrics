package com.example.songtolyrics.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.model.Music;
import com.google.gson.Gson;

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

import static com.example.songtolyrics.Parameters.API_PYTHON_METHOD;
import static com.example.songtolyrics.Parameters.API_PYTHON_PORT;
import static com.example.songtolyrics.Parameters.API_PYTHON_URL;


public class ReccordFragment extends BaseFragment {
    private static final String LOG_TAG = "AudioRecordTest";

    private static String fileName = null;

    private Button recordButton = null;
    private MediaRecorder recorder = null;

    private Button playButton = null;
    private MediaPlayer player = null;

    // TESTS PYTHON API
    private ProgressBar mProgressBar;
    private TextView mResponseView;

    // Required empty public constructor
    public ReccordFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParentview = inflater.inflate(R.layout.fragment_reccord, container, false);

        fileName = Utils.getFileName(mContext);

        recordButton    = mParentview.findViewById(R.id.audio_reccord_reccord);
        playButton      = mParentview.findViewById(R.id.audio_reccord_play);

        playButton.setOnClickListener(new View.OnClickListener() {
            boolean mStartPlaying = true;
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    playButton.setText(R.string.audio_reccord_play_end_btn);
                } else {
                    playButton.setText(R.string.audio_reccord_play_start_btn);
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            boolean mStartRecording = true;
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    recordButton.setText(R.string.audio_reccord_reccord_end_btn);
                } else {
                    recordButton.setText(R.string.audio_reccord_reccord_start_btn);
                }
                mStartRecording = !mStartRecording;
            }
        });

        mProgressBar            = mParentview.findViewById(R.id.audio_record_progressBar);
        mResponseView           = mParentview.findViewById(R.id.audio_record_responseView);
        Button testPythonAPI    = mParentview.findViewById(R.id.audio_reccord_test_python_API);
        testPythonAPI.setOnClickListener(v -> {
            mProgressBar.setVisibility(View.VISIBLE);
            mResponseView.setText("");

            RetrieveFeedTask runningTask;
            runningTask = new RetrieveFeedTask(getActivity());
            runningTask.execute();

        });
        return mParentview;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
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



    static class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private WeakReference<FragmentActivity> activityReference;

        RetrieveFeedTask(FragmentActivity context){
            activityReference = new WeakReference<>(context);
        }

        protected void onPreExecute() {
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


        protected void onPostExecute(String response) {
            // get a reference to the activity if it is still there
            FragmentActivity activity = activityReference.get();

            // Check the current activity is still running
            if (!(activity == null || activity.isFinishing())){

                ProgressBar progressBar = activity.findViewById(R.id.audio_record_progressBar);
                progressBar.setVisibility(View.GONE);

                // Check response content
                if(response == null) {
                    TextView result = activity.findViewById(R.id.audio_reccord_txt_result);
                    result.setVisibility(View.VISIBLE);
                }
                // If response isn't null <=> request received
                else {
                    Gson gson = new Gson();
                    Music music = gson.fromJson(response, Music.class);

                    // Run Lyrics search activity
                    View view = activity.findViewById(R.id.fragment_reccord);
                    ReccordFragmentDirections.ActionReccordFragmentToLyricsFragment action =
                            ReccordFragmentDirections.actionReccordFragmentToLyricsFragment(music.getArtist(), music.getTitle());
                    Navigation.findNavController(view).navigate(action);
                }
            }
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
    }
}