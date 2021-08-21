package com.example.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity {

    public static ArrayList<Song> audioList=new ArrayList<Song>();



    String[] data = {MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DISPLAY_NAME
    };
    private ListView listView;
    private MusicAdapter musicAdapter;
   // private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        GetPermission();





    }
    public void GetPermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getMp3Songs();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        GetPermission();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }
    public void getMp3Songs() {
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, data, null, null, null);
        if(audioCursor != null){
            if(audioCursor.moveToFirst()){
                do{
                    Song song=new Song();
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


                    audioList.add(song);
                }while(audioCursor.moveToNext());
            }
        }
        audioCursor.close();
        musicAdapter=new MusicAdapter(MainActivity.this,0,audioList);
        listView.setAdapter(musicAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,PlaySong.class);
                intent.putExtra("Position",position);
                startActivity(intent);
            }
        });
    }
}