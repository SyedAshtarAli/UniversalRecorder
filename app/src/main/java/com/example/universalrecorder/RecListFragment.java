package com.example.universalrecorder;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecListFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecListFragment newInstance(String param1, String param2) {
        RecListFragment fragment = new RecListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    ArrayList<RecordingsItem> recordingsItemList=new ArrayList<RecordingsItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        searchTXT(new File(getFilePath("")));
    }
    ListView listView;
    RecordingsAdapter recordingsAdapter;
    MediaPlayer mediaPlayer;
    String mFileName="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_rec_list, container, false);
        listView=view.findViewById(R.id.listView);

        recordingsAdapter=new RecordingsAdapter(getContext(),0,recordingsItemList);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordingsItem recordingsItem=(RecordingsItem) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), recordingsItem.getName(), Toast.LENGTH_SHORT).show();


            }
        });
        listView.setAdapter(recordingsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordingsItem recordingsItem=(RecordingsItem) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), recordingsItem.getName(), Toast.LENGTH_SHORT).show();


            }
        });
    }


    private void searchTXT(File dir) {
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile() && isTXT(file)) {

                recordingsItemList.add(new RecordingsItem(file.getName(),getDate(new Date(file.lastModified())) ,formatFileSize(file.length()),getDuration(file.getName().toString())));
                Log.i("TXT", file.getName());
            } else if (file.isDirectory()) {
                searchTXT(file.getAbsoluteFile());
            }
        }
    }

    String getDate(Date date){
        String dd="";
        switch(date.getMonth()) {
            case 0:
                dd=date.getDate()+" Jan";
                break;
            case 1:
                dd=date.getDate()+" Feb";
                break;
            case 2:
                dd=date.getDate()+" Mar";
                break;
            case 3:
                dd=date.getDate()+" Apr";
                break;
            case 4:
                dd=date.getDate()+" May";
                break;
            case 5:
                dd=date.getDate()+" Jun";
                break;
            case 6:
                dd=date.getDate()+" Jul";
                break;
            case 7:
                dd=date.getDate()+" Aug";
                break;
            case 8:
                dd=date.getDate()+" Sep";
                break;
            case 9:
                dd=date.getDate()+" Oct";
                break;
            case 10:
                dd=date.getDate()+" Nov";
                break;
            case 11:
                dd=date.getDate()+" Dec";
                break;

        }


        return dd;

    }

    private boolean isTXT(File file){
            boolean is = false;
            if(file.getName().endsWith(".mp3")){
                is = true;

            }
            return is;
        }

    String getDuration(String fileName){
        String filePath=getFilePath(fileName)+"/"+fileName;
            MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(filePath));
            int duration = mp.getDuration();
            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);

            return hours+":"+minutes+":"+seconds;

        }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    String getFilePath(String fileName){
        if(fileName.equals("")){
         return Environment.getExternalStorageDirectory()+"/Recordings"+fileName;

        }
        else
            return Environment.getExternalStorageDirectory()+"/Recordings";
    }


}
