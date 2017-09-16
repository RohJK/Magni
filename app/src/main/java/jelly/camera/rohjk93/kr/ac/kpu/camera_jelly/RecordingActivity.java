package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;


public class RecordingActivity extends Activity implements SurfaceHolder.Callback , Camera.AutoFocusCallback, Camera.PictureCallback{

    public static String IMAGE_FILE = "captuer.jpg";
    String sdRootPath;
    String filePath,filePath2;
    String lastFilePath;
    private static int fileIndex = 0;

    final int MIC_PERMISSION = 13;


    private static int isRecording = 1;

    protected static final String TAG = null;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera = null;
    Camera.Parameters params;
    Camera.AutoFocusCallback cb;
    private Context context = this;
    private List< String > WBitems;
    private List<Camera.Size> previewSize;
    private List<Camera.Size> supportedSize;
    private int WBSelectIndex = 0; // 화이트밸런스 선택 인덱스 상태 저장
    private int SizeSelectIndex = 0;
    private Boolean flashState = true;

    View decorView;
    int uiOptions;

    private MediaRecorder recorder;
    private CamcorderProfile camProfile;


    ArrayList<String> cameraSizeList;
    ArrayAdapter<String> cameraSizeAdapter;
    ArrayList<String> WhiteBalanceList;
    ArrayAdapter<String> WhiteBalanceAdapter;


    private CameraControl mCmcontrol;

    private ImageView watermark;
    private ImageView logoImage;
    private ImageButton shuter;

    private SeekBar zoomSeekBar;
    private int dspWidth,dspHeight;
    private int rec_width = 1920 , rec_height = 1080;

    private ImageButton flashButton;
    private ImageButton wbButton;
    private ImageButton sizeButton;
    private ImageButton helpButton;
    private ImageButton switchButton;
    private TextView zoomTextView;
    private ImageView centerImage;
    private  ImageButton galleryButton;

    public static final String WHITE_BALANCE_AUTO = "auto";
    public static final String WHITE_BALANCE_CLOUDY_DAYLIGHT = "cloudy-daylight";
    public static final String WHITE_BALANCE_DAYLIGHT = "daylight";
    public static final String WHITE_BALANCE_FLUORESCENT = "fluorescent";
    public static final String WHITE_BALANCE_INCANDESCENT = "incandescent";
    public static final String WHITE_BALANCE_SHADE = "shade";
    public static final String WHITE_BALANCE_TWILIGHT = "twilight";
    public static final String WHITE_BALANCE_WARM_FLUORESCENT="warm-fluorescent";

    public RecordingActivity() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// display always on
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상단바 제거
        setContentView(R.layout.activity_recording);


        if(Build.VERSION.SDK_INT >= 23)
        {
            checkPermission();
        }

        decorView = getWindow().getDecorView();

        surfaceView = (SurfaceView)findViewById(R.id.RECCAMERAsurfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // 스크린 크기를 가져옴
/*
        int[] maxTextureSize = new int[1];
        Rect rect = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);

        */

        if (Build.VERSION.SDK_INT >= 11) {
            Point size = new Point();
            try {
                this.getWindowManager().getDefaultDisplay().getRealSize(size);
                dspWidth = size.x;
                dspHeight = size.y;
            } catch (NoSuchMethodError e) {
                Log.i("error", "it can't work");
            }
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            dspWidth = metrics.widthPixels;
            dspHeight = metrics.heightPixels;
        }

        camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

        /*
        dspWidth = rect.width();
        dspHeight = rect.bottom;
        if(dspWidth == 1080)
            dspHeight = 1920;
        if(dspWidth == 720)
            dspHeight = 1280;
            */

        flashButton = (ImageButton)findViewById(R.id.RECFLASHBUTTON);
        wbButton = (ImageButton)findViewById(R.id.RECWBBUTTON);
        sizeButton = (ImageButton)findViewById(R.id.RECSIZEbutton);
        helpButton = (ImageButton)findViewById(R.id.RECHELPBUTTON);
        switchButton = (ImageButton)findViewById(R.id.SWITCHCAPTURE);
        zoomTextView = (TextView)findViewById(R.id.RECtextView);
        centerImage = (ImageView)findViewById(R.id.CENTERIMAGE);
        //final ImageButton recButton = (ImageButton)findViewById(R.id.RECBUTTON);
        shuter = (ImageButton)findViewById(R.id.RECBUTTON);
        watermark = (ImageView)findViewById(R.id.RECWATERMARKIMAGEVIEW);
        logoImage = (ImageView)findViewById(R.id.RECLOGOIMAGEVIEW);
        galleryButton = (ImageButton)findViewById(R.id.GALLERYBUTTON);

        zoomSeekBar = (SeekBar)findViewById(R.id.RECZOOMSEEKBAR);

        //워터마크 투명도 조절
        watermark.setAlpha(70);
        centerImage.setAlpha(70);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("content://media/external/images/media"));
                //intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivity(intent);
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InformationActivity.class);
                startActivity(intent);
            }
        });


        // 줌 조절 seekBar
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                params.setZoom(progress);
                mCamera.setParameters(params);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == helpButton)
                {
                    final CharSequence[] items = {"회사소개", "도움말"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordingActivity.this).setTitle("HELP");

                    builder.setItems(items,new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int index)
                        {
                            if(index == 0)
                            {
                                Intent intent = new Intent(getApplicationContext(),InformationActivity.class);
                                startActivity(intent);
                            }
                            if(index == 1)
                            {
                                Intent intent = new Intent(getApplicationContext(),AdviceActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        sizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == sizeButton)
                {

                    AlertDialog.Builder builder  = new AlertDialog.Builder(RecordingActivity.this).setTitle("RESOLUTION")
                            .setSingleChoiceItems(cameraSizeAdapter,SizeSelectIndex,new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String[] SizeTokenstrings = new String[2]; // SizeTokenstrings[0] = Camera.width  SizeTokenstrings[1] = Camera.height
                                    int cnt = 0;

                                    StringTokenizer string = new StringTokenizer(cameraSizeAdapter.getItem(which).toString(),"*");
                                    while(string.hasMoreTokens())
                                    {
                                        SizeTokenstrings [cnt]= string.nextToken();
                                        cnt++;
                                        if(string.equals("*")){
                                            continue;
                                        }
                                    }

                                    int cameraWidth = Integer.valueOf(SizeTokenstrings[0]);
                                    int cameraHeight = Integer.valueOf(SizeTokenstrings[1]);

                                    rec_width = cameraWidth;
                                    rec_height = cameraHeight;

                                    if((cameraWidth*9)==(cameraHeight*16))
                                        watermark.setImageResource(R.drawable.rsz_watermark16_9);
                                    else{
                                        watermark.setImageResource(R.drawable.rsz_watermark4_3);
                                    }

                                    for(Camera.Size preSize : previewSize)
                                    {
/*
                                        // 프리뷰 사이즈가 디스플레이 하드웨어 크기보다 클 경우 컨티뉴 - 저해상도 폰 위한 옵션
                                        if(dspWidth < preSize.height || dspHeight < preSize.width)
                                            continue;
 */


                                        if(((preSize.width*9)==(preSize.height*16)) /*|| ((preSize.width*3)==(preSize.height*4))*/)

                                        {
                                            if ((preSize.width * cameraHeight) == (preSize.height * cameraWidth)) {

                                                if((cameraWidth*9)==(cameraHeight*16))
                                                {
                                                    int custumScreensizeWidth = preSize.height * dspHeight / preSize.width;
                                                    params.setPreviewSize(preSize.width, preSize.height);
                                                    mHolder.setFixedSize(custumScreensizeWidth, dspHeight);
                                                    params.setPictureSize(cameraWidth, cameraHeight);
                                                    watermark.setMaxWidth(custumScreensizeWidth);
                                                    // watermark.setMaxHeight(custumScreensizeHeight);
                                                    watermark.setMinimumWidth(custumScreensizeWidth);
                                                    // watermark.setMinimumHeight(custumScreensizeHeight);

                                                    break;
                                                }

                                                int custumScreensizeHeight = preSize.width * dspWidth / preSize.height;
                                                params.setPreviewSize(preSize.width, preSize.height);
                                                mHolder.setFixedSize(dspWidth, custumScreensizeHeight);
                                                params.setPictureSize(cameraWidth, cameraHeight);
                                                watermark.setMaxWidth(dspWidth);
                                               // watermark.setMaxHeight(custumScreensizeHeight);
                                                watermark.setMinimumWidth(dspWidth);
                                               // watermark.setMinimumHeight(custumScreensizeHeight);




                                                break;
                                            }
                                        }
                                    }

                                    SizeSelectIndex = which;

                                    mCamera.setParameters(params);
                                    // 프리뷰 시작
                                    mCamera.startPreview();

                                    dialog.cancel();

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        //화이트밸런스
        wbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == wbButton)
                {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(RecordingActivity.this).setTitle("WHITE BALANCE")
                            .setSingleChoiceItems(WhiteBalanceAdapter,WBSelectIndex,new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int index){

                                    params.setWhiteBalance(WhiteBalanceAdapter.getItem(index));
                                    mCamera.setParameters(params);
                                    WBSelectIndex = index;
                                    Toast.makeText(getApplicationContext(),WhiteBalanceAdapter.getItem(index),Toast.LENGTH_SHORT).show();


                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        // 오토포커스 터치 리스너
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(cb);
            }
        });
        // FLASH ON / OFF 버튼 리스너
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flashState) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                    flashState = false;
                    flashButton.setImageResource(R.drawable.flash_off);
                }else{
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(params);
                    flashButton.setImageResource(R.drawable.flash_on);
                    flashState = true;
                }
            }

        });

/*
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder == null)
                {
                    try {
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

                    sdRootPath = "/storage/sdcard0/DCIM";
                    filePath = sdRootPath + "/MAGNIRecorder_"+System.currentTimeMillis() +".mp4";

                    recorder.setPreviewDisplay(mHolder.getSurface());

                        recorder.prepare();
                        recorder.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                        recorder.release();
                        recorder = null;
                    }


                }



            }
        });
*/


    }


    @Override
    protected void onResume() {

        uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onResume();
    }


    @Override
    protected void onRestart() {


        WBSelectIndex = 0; // 화이트밸런스 선택 인덱스 상태 저장
        SizeSelectIndex = 0;


        Camera.Size size =  params.getPictureSize();
        /*
        if((size.width*9)==(size.height*16)) {
            watermark.setImageResource(R.drawable.rsz_watermark16_9);
        }
        else{
            watermark.setImageResource(R.drawable.rsz_watermark4_3);
        }
        */
        if((size.width*3)==(size.height*4))
            watermark.setImageResource(R.drawable.rsz_watermark4_3);
        super.onRestart();
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        // 카메라에 스크린 지정
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);

            // 프리뷰 크기 목록을 구한다
            params = mCamera.getParameters();

            mCmcontrol = new CameraControl(this.getApplicationContext(),mCamera,params,dspWidth,dspHeight);

            // 지원하는 화이트밸런스 목록을 받아 저장
            WBitems = params.getSupportedWhiteBalance ();

            zoomSeekBar.setMax(params.getMaxZoom());

            // 촬영가능한 size 목록을 받아 저장
            supportedSize = params.getSupportedVideoSizes();
            previewSize = params.getSupportedPreviewSizes();
            Camera.Size maxCMsize  = supportedSize.get(0);
            rec_width = maxCMsize.width;
            rec_height = maxCMsize.height;

            cameraSizeList = new ArrayList<>();

            for(Camera.Size size : supportedSize)
            {
                if((size.width == 1920) || ((size.width == 1280) && (size.height == 720))) {
                    cameraSizeList.add(String.valueOf(size.width) + "*" + String.valueOf(size.height));
                }else{
                    continue;
                }
            }
            cameraSizeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,cameraSizeList);

            WhiteBalanceList = new ArrayList<>();
            for(String string : WBitems) {
                WhiteBalanceList.add(string);
            }
            WhiteBalanceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,WhiteBalanceList);

            if((maxCMsize.width*9)==(maxCMsize.height*16))
                watermark.setImageResource(R.drawable.rsz_watermark16_9);

            for(Camera.Size preSize : previewSize)
            {

                /*
                // 프리뷰 사이즈가 디스플레이 하드웨어 크기보다 클 경우 컨티뉴 - 저해상도 폰 위한 옵션
                if(dspWidth < preSize.height || dspHeight < preSize.width)
                    continue;
*/


                if((preSize.width * maxCMsize.height) == (preSize.height * maxCMsize.width))
                {
                    int custumScreensizeHeight = preSize.width * dspWidth / preSize.height;
                    params.setPreviewSize(preSize.width,preSize.height);
                    mHolder.setFixedSize(dspWidth,custumScreensizeHeight);
                   // mHolder.setFixedSize(dspWidth,dspHeight);
                    params.setPictureSize(maxCMsize.width,maxCMsize.height);
                    watermark.setMaxWidth(dspWidth);
                    //watermark.setMaxHeight(custumScreensizeHeight);
                    watermark.setMinimumWidth(dspWidth);
                    //watermark.setMinimumHeight(custumScreensizeHeight);


                    break;
                }
            }


            params = mCmcontrol.initParameter();

            mCamera.setParameters(params);
            // 프리뷰 시작
            mCamera.startPreview();


        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
            return;
        }
    }

    public void onClick(View v) {

        if(isRecording == 0)
        {
            stopRecording();
            shuter.setImageResource(R.drawable.ic_action_video);
            isRecording = 1;
            Toast.makeText(getApplicationContext(), "저장완료", Toast.LENGTH_LONG).show();

             flashButton.setVisibility(View.VISIBLE);
             wbButton.setVisibility(View.VISIBLE);
             sizeButton.setVisibility(View.VISIBLE);
             helpButton.setVisibility(View.VISIBLE);
             switchButton.setVisibility(View.VISIBLE);
            logoImage.setVisibility(View.VISIBLE);
            zoomSeekBar.setVisibility(View.VISIBLE);
            zoomTextView.setVisibility(View.VISIBLE);
            centerImage.setVisibility(View.VISIBLE);
            galleryButton.setVisibility(View.VISIBLE);

        }else {
            prepareVideoRecorder();
            //Toast.makeText(getApplicationContext(), "촬영시작", Toast.LENGTH_LONG).show();
            flashButton.setVisibility(View.INVISIBLE);
            wbButton.setVisibility(View.INVISIBLE);
            sizeButton.setVisibility(View.INVISIBLE);
            helpButton.setVisibility(View.INVISIBLE);
            switchButton.setVisibility(View.INVISIBLE);
            logoImage.setVisibility(View.INVISIBLE);
            zoomSeekBar.setVisibility(View.INVISIBLE);
            zoomTextView.setVisibility(View.INVISIBLE);
            centerImage.setVisibility(View.INVISIBLE);
            galleryButton.setVisibility(View.INVISIBLE);
            shuter.setImageResource(R.drawable.ic_action_stop);
        }
}

    public void stopRecording() {
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {



    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if( mCamera != null ) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

        }
    }



    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
// 이미지 파일 경로 생성
         sdRootPath = "/storage/sdcard0/DCIM/Magni";
         filePath = sdRootPath + "/MAGNICapture_"+System.currentTimeMillis() +".jpg";

        Bitmap rotateBitmap = mCmcontrol.imgaeRotate(data);

        // 이미지를 파일로 저장
        File file = new File(filePath);
        try {

            FileOutputStream fos = new FileOutputStream(file);
            rotateBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.write(data);
            fos.flush();
            fos.close();
            context.sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)) );

            Intent intent = new Intent(getApplicationContext(),CaptureCheckActivity.class);
            intent.putExtra("SavefilePath",filePath);
            startActivity(intent);
            finish();

        } catch (IOException e) {
            Log.d("tag", "File Write Error");
            return;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            //mCamera.autoFocus(this);
        }
        else if(keyCode == KeyEvent.KEYCODE_BACK)
            if(isRecording != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordingActivity.this).setTitle("Magni");

                builder.setMessage("종료하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        return true;
    }


    public boolean prepareVideoRecorder() {
        recorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        if (mCamera == null) {
            return false;
        }
        mCamera.unlock();
        recorder.setCamera(mCamera);
        isRecording = 0;
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        //recorder.setVideoFrameRate(60);
        recorder.setProfile(camProfile);
        recorder.setOrientationHint(90);
//        recorder.setProfile(camProfile);
        //filePath = String.format("/storage/sdcard0/");
        filePath = String.valueOf(Environment.getExternalStorageDirectory());
        filePath2 = String.format("/DCIM/Magni/MAGNIRec"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".mp4");
        lastFilePath = filePath + filePath2;
        recorder.setOutputFile(lastFilePath);
        //recorder.setMaxDuration(60000); // Set max duration 60 sec.
        //recorder.setMaxFileSize(5000000); // Set max file size 5M
        recorder.setVideoSize(rec_width,rec_height);
        recorder.setPreviewDisplay(mHolder.getSurface());
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (Exception e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void releaseMediaRecorder() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.reset();   // clear recorder configuration
                recorder.release(); // release the recorder object
                recorder = null;
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED), String.valueOf(Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+lastFilePath)));
                mCamera.lock();           // lock camera for later use
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error during releaseMediaRecorder");
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.onDestroy();
        mHolder = null;
        releaseCamera();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHolder = null;
        releaseCamera();
    }
    //


    public void checkPermission()
    {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission_group.MICROPHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestMicPermission();

        } else {
            /*Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
            startActivity(intent);
            finish();*/
        }
    }

private void requestMicPermission() {
        Log.i(TAG, "STORAGE permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying STORAGE permission rationale to provide additional context.");
           /*Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(IntroActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();*/

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION);

            // 다이얼로그로 처리해야함
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION);
        }
        // END_INCLUDE(camera_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MIC_PERMISSION) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for MIC permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "MIC permission has now been granted. Showing preview.");
                //Snackbar.make(mLayout, R.string.permision_available_camera,
                //        Snackbar.LENGTH_SHORT).show();

            } else {
                Log.i(TAG, "MIC permission was NOT granted.");
                Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                startActivity(intent);
                finish();
            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
