package com.test.vedioview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by dell-pc on 2017/11/24.
 */

public class secondActivity extends Activity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener{
    private VideoView videoView;
    private Uri uri;
    String path;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.all);
        videoView = (VideoView)findViewById(R.id.vedioView2);
        //设置播放完成以后监听
        Intent intent=getIntent();
         path=intent.getStringExtra("path");
//         uri=getIntent().getData();
//        videoView.setVideoURI(uri);
//        Log.i(uri.toString(),"a");
        videoView.setVideoPath(path);
        Toast.makeText(getApplicationContext(),path,Toast.LENGTH_LONG).show();
        videoView.start();
        videoView.setMediaController(new MediaController(this));
        Log.i(path,"msg");
        videoView.setOnCompletionListener(this);
        //设置发生错误监听，如果不设置videoview会向用户提示发生错误
        videoView.setOnErrorListener(this);
        //设置在视频文件在加载完毕以后的回调函数
        videoView.setOnPreparedListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
