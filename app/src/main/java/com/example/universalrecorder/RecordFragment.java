package com.example.universalrecorder;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.universalrecorder.MainActivity.REQUEST_AUDIO_PERMISSION_CODE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ImageButton recBtn,pauBtn,resBtn,stopBtn;
    TextView timeElapsed;
    private MediaRecorder mRecorder;
    SharedPreferences sharedPreferences;
    final String fileKey="fileKey";
    int val=1;
    private static String mFileName = "Recording";
    private static final String LOG_TAG = "AudioRecording";
    private MediaPlayer mPlayer;
    int counter=0;
    CountDownTimer countDownTimer;

    boolean running;
    boolean started;
    LinearLayout.LayoutParams params;

    Chronometer chronometer;
    private long pauseOffset;

    int recNum;

    AlertDialog alertDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_record, container, false);
        recBtn=view.findViewById(R.id.recBtn);
        pauBtn=view.findViewById(R.id.pauBtn);
        resBtn=view.findViewById(R.id.ressBtn);
        stopBtn=view.findViewById(R.id.stopBtn);

        stopBtn.setEnabled(false);
        stopBtn.setAlpha((float) 0.5);
        pauBtn.setEnabled(false);
        pauBtn.setAlpha((float) 0.5);
        resBtn.setEnabled(false);
        resBtn.setAlpha((float) 0.5);

        chronometer=(Chronometer) view.findViewById(R.id.timeElapsed);
        chronometer.setFormat("Recording: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                /*if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 10000) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(getActivity(), "Bing!", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        sharedPreferences=getActivity().getSharedPreferences("MyPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(sharedPreferences.getInt(fileKey,-1)==-1) {
            editor.putInt(fileKey, val);
            editor.commit();
            Toast.makeText(getActivity(), "Initial to SharedPref", Toast.LENGTH_SHORT).show();
            mFileName=mFileName+val+".mp3";
        }
        else {
            recNum = sharedPreferences.getInt(fileKey, -1);
            mFileName=mFileName+recNum+".mp3";
        }



        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Rec", Toast.LENGTH_SHORT).show();




                if(CheckPermissions() && checkDirectory()) {
                    recBtn.setEnabled(false);
                    pauBtn.setEnabled(true);
                    resBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    recBtn.setAlpha((float)0.5);
                    pauBtn.setAlpha((float)1);
                    resBtn.setAlpha((float)0.5);
                    stopBtn.setAlpha((float)1);

                    startChronometer(v);

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


                    Toast.makeText(getActivity(), recNum+"", Toast.LENGTH_SHORT).show();
                    mFileName = getDirectoryPath();
                    mFileName += "/Recording"+recNum+".mp3";

                    mRecorder.setOutputFile(mFileName);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putInt(fileKey,++recNum);
                    editor.commit();

                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                    mRecorder.start();
                    started=true;

                }
                else
                {
                    RequestPermissions();
                }

            }
        });
        resBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                recBtn.setEnabled(false);
                pauBtn.setEnabled(true);
                resBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                recBtn.setAlpha((float)0.5);
                pauBtn.setAlpha((float)1);
                resBtn.setAlpha((float)0.5);
                stopBtn.setAlpha((float)1);


               if(started)
                   startChronometer(v);

               mRecorder.resume();
                Toast.makeText(getActivity(), "Recording Resumed", Toast.LENGTH_SHORT).show();
            }
        });
        pauBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                recBtn.setEnabled(false);
                pauBtn.setEnabled(false);
                resBtn.setEnabled(true);
                stopBtn.setEnabled(true);
                recBtn.setAlpha((float)0.5);
                pauBtn.setAlpha((float)0.5);
                resBtn.setAlpha((float)1);
                stopBtn.setAlpha((float)1);

                chronometer.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                mRecorder.pause();
                Toast.makeText(getActivity(), "Recording Paused ", Toast.LENGTH_SHORT).show();
                pauseChronometer(v);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recBtn.setEnabled(true);
                pauBtn.setEnabled(false);
                resBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                recBtn.setAlpha((float)1);
                pauBtn.setAlpha((float)0.5);
                resBtn.setAlpha((float)0.5);
                stopBtn.setAlpha((float)0.5);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.confirmation_dialog, viewGroup, false);
                builder.setView(dialogView);
                Button saveBtn=dialogView.findViewById(R.id.save_btn);
                Button cancelBtn=dialogView.findViewById(R.id.cancel_btn);
                final EditText recNameEdt=dialogView.findViewById(R.id.name_edt);
                recNameEdt.setText("Recording"+(recNum-1)+".mp3");
                alertDialog = builder.create();
                alertDialog.show();
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mRecorder.setOutputFile(getDirectoryPath()+recNameEdt.getText().toString()+".mp3");
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                        resetChronometer(v);
                        Toast.makeText(getActivity().getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
                        alertDialog.hide();

                        Toast.makeText(getActivity(), "save Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                        recNameEdt.setText("");
                        resetChronometer(v);
                        Toast.makeText(getActivity().getApplicationContext(), "Recording Canceled", Toast.LENGTH_LONG).show();
                        mRecorder.reset();
                        mRecorder=null;
                    }
                });
                chronometer.stop();
                resetChronometer(view);


            }
        });



        // Inflate the layout for this fragment
        return view;
    }


    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    String getDirectoryPath(){
        String myFolder= Environment.getExternalStorageDirectory()+"/Recordings";
        return myFolder;
    }

    boolean checkDirectory(){
        String myFolder=Environment.getExternalStorageDirectory()+"/";
        File file= new File(myFolder, "Recordings");

        if(!file.exists()) {
            if(file.mkdir()); //directory is created;
        }

        return true;

    }

    public void startChronometer(View v) {
        if (!running) {
            started=true;
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }
    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
    public void resetChronometer(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

}

