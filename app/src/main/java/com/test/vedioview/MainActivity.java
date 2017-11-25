package com.test.vedioview;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private VideoView videoView ;
    private Uri mUri;
    private Context mContext;
    String uriPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//获取手机屏幕大小
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置屏幕不息屏
        setContentView(R.layout.activity_main);
        //mUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid);
        //本地的视频 需要在手机SD卡根目录添加一个 fl1234.mp4 视频
       // String videoUrl1 = Environment.getExternalStorageDirectory().getPath()+"/fl1234.mp4" ;

        //网络视频
       // String videoUrl2 = Utils.videoUrl ;

        //Uri uri = Uri.parse( videoUrl2 );
        mContext=this;
        videoView = (VideoView)this.findViewById(R.id.vedioView );
         Button getvideo=(Button)findViewById(R.id.btn_getVideo);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.pause).setOnClickListener(this);
        findViewById(R.id.infor).setOnClickListener(this);
        findViewById(R.id.another).setOnClickListener(this);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }//获取存储权限
         getvideo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent itVideo = new Intent(Intent.ACTION_GET_CONTENT);
                 itVideo.setType("video/*");
                 startActivityForResult(itVideo,12);
             }
         });
        mUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid);
        uriPath = mUri.toString();
        videoView.setVideoPath(uriPath);

        //设置视频控制器
        videoView.setMediaController(new MediaController(this));

        //播放完成回调
        videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());

        //设置视频路径
        videoView.setVideoURI(mUri);
         //videoView.setVideoPath(uriPath);
        //开始播放视频
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.btn_getVideo:
//             Intent itVideo = new Intent(Intent.ACTION_GET_CONTENT);
//               itVideo.setType("video/*");
//             startActivityForResult(itVideo,12);
//            Toast.makeText(this,"qqqq",Toast.LENGTH_SHORT);
//              break;
            case R.id.start:
                videoView.start();
                break;
            case R.id.pause:
                videoView.pause();
                break;
            case R.id.infor:
                Toast.makeText(this,"视频时长为"+(videoView.getDuration())/1000+"秒",Toast.LENGTH_LONG).show();
                break;
            case R.id.another:
                Intent intent=new Intent(getApplicationContext(),secondActivity.class);
                intent.putExtra("path",uriPath);
                Log.i(uriPath.toString(),"路径");
                //intent.setData(mUri);
                startActivity(intent);

     }
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText( MainActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 12:
                    //data.getData()获取用户选取的Uri
                    if(Build.VERSION.SDK_INT >= 19){
                        handleMediaOkKitKat(data);
                    }else {
                        handleMediaBeforeKitKat(data);
                    }
//                    Intent intent=new Intent();
//                    intent.setClass(MainActivity.this,secondActivity.class);
//                    intent.putExtra("path",uriPath);
//                    intent.setData(mUri);
                    videoView.setVideoPath(uriPath);
//                    startActivity(intent);
                    //返回mUri的最后一段文字，即文件名部分
                    break;
            }
        }
    }

    private void handleMediaBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        uriPath = getMediaPath(uri,null);

    }

    private void handleMediaOkKitKat(Intent data) {
        Uri uri = data.getData();
        Log.d("uri=intent.getData :",""+uri);
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);        //数据表里指定的行
            Log.d("getDocumentId(uri) :",""+docId);
            Log.d("uri.getAuthority() :",""+uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                uriPath  = getMediaPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                uriPath  = getMediaPath(contentUri,null);
            }

        }
        else if ("content".equalsIgnoreCase(uri.getScheme())){
            uriPath  = getMediaPath(uri,null);
        }
    }
    //获取多媒体文件的Uri路径
    public String getMediaPath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);   //内容提供器
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));   //获取路径
            }
            cursor.close();
        }
        return path;
    }
}
