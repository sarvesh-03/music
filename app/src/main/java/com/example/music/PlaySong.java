package com.example.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

@RequiresApi(api = Build.VERSION_CODES.R)
public class PlaySong extends AppCompatActivity {

    private ImageButton playNext,playPrevious,play;
    private TextView title,duration;
    private SeekBar seekBar;
    private int position;
    private Song CurrentSong;
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songList=new ArrayList<>();
    String[] data = {MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DISPLAY_NAME
    };
    private java.util.Timer Timer;
    private boolean Mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        getSongs();
        play=findViewById(R.id.play_pause);
        playNext=findViewById(R.id.play_next);
        playPrevious=findViewById(R.id.play_previous);
        title=findViewById(R.id.song_title);
        duration=findViewById(R.id.duration);
        seekBar=findViewById(R.id.seekBar);


        position=getIntent().getIntExtra("Position",0);
        if(songList.size()!=0){
            CurrentSong=songList.get(position);
            UpdateUi(CurrentSong);
        }
        PlaySong.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int Current=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(Current);
                    if(mediaPlayer.getCurrentPosition()>=mediaPlayer.getDuration()) {
                        playNext.performClick();
                        seekBar.setProgress(0);
                        Log.v("hello",formattedTime(Current));

                    }
                    duration.setText(formattedTime(Current));



                }
                handler.postDelayed(this::run,1000);
            }
        });


        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_700),PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress()*1000);

            }
        });





        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    Mode=true;
                    mediaPlayer.pause();
                }
                else {
                    play.setImageResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();


                }

            }
        });

        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position!=songList.size()-1){
                    position++;
                    CurrentSong=songList.get(position);
                    UpdateUi(CurrentSong);
                }
                else Toast.makeText(getApplicationContext(),"Last Song",Toast.LENGTH_SHORT).show();

            }
        });

        playPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position!=0){
                    position--;
                    CurrentSong=songList.get(position);
                    UpdateUi(CurrentSong);
                }
                else Toast.makeText(getApplicationContext(),"First Song",Toast.LENGTH_SHORT).show();


            }
        });


    }

    public void getSongs() {
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, data, null, null, null);
        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    Song song = new Song();
                    //int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int filetitle = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    int file_Artist = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    int fileAlbum = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                    int filedata = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    song.setData(audioCursor.getString(filedata));
                    song.setUri(Uri.fromFile(new File(song.getData())));
                    song.setAlbum(audioCursor.getString(fileAlbum));
                    song.setArtist(audioCursor.getString(file_Artist));
                    song.setTitle(audioCursor.getString(filetitle));


                    songList.add(song);
                } while (audioCursor.moveToNext());
            }
        }
        audioCursor.close();
    }
    Handler handler=new Handler();
    public void UpdateUi(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getUri());
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        play.setImageResource(R.drawable.ic_baseline_pause_24);
        title.setText(song.getTitle());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
    private String formattedTime(int mCuurentPosition) {

        String totalout = "";
        String totalnew = "";
        String seconds = String.valueOf(mCuurentPosition%60);
        String minutes = String.valueOf(mCuurentPosition/60);
        totalout = minutes + ":" + seconds;
        totalnew = minutes + ":" + "0" + seconds;
        if(seconds.length() ==1){
            return totalnew;
        }
        else {
            return  totalout;
        }
    }
}