package com.example.universalrecorder;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecordingsAdapter extends ArrayAdapter<RecordingsItem> {


    Context ctx;
    RecordingsItem item;
    View view;
    public RecordingsAdapter(@NonNull Context context, int resource, @NonNull List<RecordingsItem> objects) {
        super(context, resource, objects);
        ctx=context;

    }

    AlertDialog alertDialog;
    MediaPlayer mediaPlayer;
    TextView name,size,date,duration;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        view=convertView;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(R.layout.audio_item, null);
        }

         item=getItem(position);
        if(item!=null){

            final ImageButton imageButton;
            name=view.findViewById(R.id.recTitle);
            size=view.findViewById(R.id.recSize);
            date=view.findViewById(R.id.recDate);
            duration=view.findViewById(R.id.recDuration);
            imageButton=view.findViewById(R.id.playBtn);

            name.setText(item.getName());
            name.setTag(item.getName());
            size.setText(item.getSize());
            date.setText(item.getDate());
            duration.setText(item.getLength());
            imageButton.setTag(item.getName());

            view.setTag("notPlaying");

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag=imageButton.getTag().toString();
                    String rec=view.findViewById(R.id.recTitle).getTag().toString();
                    String viewTag=view.getTag().toString();
                    if(viewTag.equals("notPlaying")){
                        imageButton.setImageResource(R.drawable.pause);
                        //imageButton.setTag("pause");
                        view.setTag("playing");
                        try {
                            playAudio(view, imageButton,tag);
                        } catch (IOException e) {
                            Toast.makeText(ctx, "prepare() failed", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });

        }
        ConstraintLayout.LayoutParams params=new ConstraintLayout.LayoutParams( ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(20, 50, 0, 10);
        view.setLayoutParams(params);

        return view;
    }

    int startTime=0, finalTime=0;
    SeekBar seekBar;
    static int oneTimeOnly=0;
    TextView txt1,txt2;
    private Handler myHandler = new Handler();
    String mFileName=getDirectoryPath();

    void playAudio(View v, final ImageButton imageButton, String fileName) throws IOException {
        mFileName=getDirectoryPath();
        oneTimeOnly=0;
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        ViewGroup viewGroup = view.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.rec_player, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
        mediaPlayer=new MediaPlayer();
        seekBar=dialogView.findViewById(R.id.seekBar);
        seekBar.setClickable(false);
        txt1=dialogView.findViewById(R.id.startTime);
        txt2=dialogView.findViewById(R.id.endTime);

        mFileName=mFileName+ fileName;
        mediaPlayer.setDataSource(mFileName);
        mediaPlayer.prepare();
        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        txt2.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );

        txt1.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );


        myHandler.postDelayed(UpdateSongTime,100);


        if (oneTimeOnly == 0) {
            seekBar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }




        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                imageButton.setImageResource(R.drawable.play);
                view.setTag("notPlaying");
                mediaPlayer.release();
                mediaPlayer = null;
                seekBar=null;
                alertDialog.cancel();
                myHandler.removeCallbacks(UpdateSongTime);
                startTime=0;
                finalTime=0;


            }
        });

    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            txt1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekBar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };


    String getDirectoryPath(){
        String myFolder= Environment.getExternalStorageDirectory()+"/Recordings/";
        return myFolder;
    }
}
