package com.ibrahima.tonegentest;

import android.app.ListActivity;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.media.ToneGenerator;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.lang.reflect.Field;

/**
 * This is just a one off program I made while trying to debug a bug in the CyanogenMod port for the Fascinate.
 *
 * Use it however you like; I doubt there's anything useful in it anyway, but I'm publishing the source because
 * I didn't see any examples of anyone using the android.media.ToneGenerator class anywhere.
 */
public class ToneGenTest extends ListActivity {
    String TAG;
    Field[] fields;

    public void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        TAG = res.getString(R.string.app_name);
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Tone generator test started");
        Class<ToneGenerator> c = ToneGenerator.class;
        fields = c.getDeclaredFields();
        setListAdapter(new ArrayAdapter<Field>(this, R.layout.list_item, fields){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // re-use the convertView, try not to recreate objects here or inflate every time (expensive)
                // also use the "tag" with the "view holder" pattern to avoid findViewById every time
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater=getLayoutInflater();
                    v=inflater.inflate(R.layout.list_item, parent, false);
                }
                v.setTag(fields[position]);
                TextView tv = (TextView)v;
                tv.setText(fields[position].getName());
                return tv;
            }
        });
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            ToneGenerator toneGen;
            {
                toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
            }
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Field f = (Field)(((TextView) view).getTag());
                String fName = f.getName();
                try{
                    int fInt = f.getInt(toneGen);
                    //For some reason, I guess because of the sleep call, this shows up after the sound is over
                    Toast.makeText(getApplicationContext(), "Playing "+f.getName(),
                            Toast.LENGTH_SHORT).show();
                    if(fName.startsWith("TONE_")){
                        if (toneGen.startTone(fInt)) {
                            Thread.sleep(1000);
                            toneGen.stopTone();
                            Thread.sleep(500);
                        }
                    }
                }catch (Exception e){//Yeah yeah this is a one off thing
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "Tone Generator test has exited");
    }
}

