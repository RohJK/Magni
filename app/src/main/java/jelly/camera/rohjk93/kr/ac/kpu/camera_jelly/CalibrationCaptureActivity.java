package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class CalibrationCaptureActivity extends Activity implements SurfaceHolder.Callback , Camera.AutoFocusCallback, Camera.PictureCallback{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int saveImageHeight, saveImageWidth;

    float calibValue;

    private final int SETUP =1001;
    private final int CALIB =2001;

    Intent intent2;

    public static String IMAGE_FILE = "captuer.jpg";
    String sdRootPath;
    String filePath;
    private static int fileIndex = 0;

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
    private int zoomValue;
    private Boolean flashState = true;

    View decorView;
    int uiOptions;

    private MediaRecorder recorder;


    ArrayList<String> cameraSizeList;
    ArrayAdapter<String> cameraSizeAdapter;
    ArrayList<String> WhiteBalanceList;
    ArrayAdapter<String> WhiteBalanceAdapter;


    private CameraControl mCmcontrol;

    private ImageView watermark;
    private ImageView logoImage;

    private SeekBar zoomSeekBar;
    private int dspWidth,dspHeight;

    public static final String WHITE_BALANCE_AUTO = "auto";
    public static final String WHITE_BALANCE_CLOUDY_DAYLIGHT = "cloudy-daylight";
    public static final String WHITE_BALANCE_DAYLIGHT = "daylight";
    public static final String WHITE_BALANCE_FLUORESCENT = "fluorescent";
    public static final String WHITE_BALANCE_INCANDESCENT = "incandescent";
    public static final String WHITE_BALANCE_SHADE = "shade";
    public static final String WHITE_BALANCE_TWILIGHT = "twilight";
    public static final String WHITE_BALANCE_WARM_FLUORESCENT="warm-fluorescent";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// display always on
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상단바 제거
        setContentView(R.layout.activity_calibration_capture);

        pref = getSharedPreferences("SAVECAL",MODE_PRIVATE);
        editor = pref.edit();

        SizeSelectIndex = pref.getInt("SIZEINDEX",0);
        saveImageHeight = pref.getInt("HEIGHT",0);
        saveImageWidth = pref.getInt("WIDTH",0);

        decorView = getWindow().getDecorView();

        surfaceView = (SurfaceView)findViewById(R.id.CAMERAsurfaceView);
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

        /*
        dspWidth = rect.width();
        dspHeight = rect.bottom;
        if(dspWidth == 1080)
            dspHeight = 1920;
        if(dspWidth == 720)
            dspHeight = 1280;
            */

        final ImageButton flashButton = (ImageButton)findViewById(R.id.FLASHBUTTON);
        final ImageButton wbButton = (ImageButton)findViewById(R.id.WBBUTTON);
        final ImageButton sizeButton = (ImageButton)findViewById(R.id.SIZEbutton);
        final ImageButton helpButton = (ImageButton)findViewById(R.id.HELPBUTTON);
        final ImageButton recButton = (ImageButton)findViewById(R.id.RECBUTTON);
        final ImageView centerImage = (ImageView)findViewById(R.id.CENTERIMAGE2);
        final ImageButton switchButton = (ImageButton)findViewById(R.id.SWITCHRECORDER);
        final ImageButton galleryButton = (ImageButton)findViewById(R.id.GALLERYBUTTON2);
        watermark = (ImageView)findViewById(R.id.WATERMARKIMAGEVIEW);
        logoImage = (ImageView)findViewById(R.id.LOGOIMAGEVIEW);

        zoomSeekBar = (SeekBar)findViewById(R.id.ZOOMSEEKBAR);

        //워터마크 투명도 조절
        watermark.setAlpha(70);
        centerImage.setAlpha(70);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/external/images/media"));
                //intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivity(intent);
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dspWidth < 720)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalibrationCaptureActivity.this);
                    builder.setTitle("")        // 제목 설정
                            .setMessage("저해상도 스마트폰은 지원하지 않습니다")        // 메세지 설정
                            .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성

                    dialog.show();    // 알림창 띄우기
                }

                else if(dspWidth >= 720) {
                    Intent intent = new Intent(getApplicationContext(), RecordingActivity.class);
                    startActivity(intent);
                    finish();
                }
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
                zoomValue = progress;
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
                    final CharSequence[] items = {"회사소개", "도움말","설정"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(CalibrationCaptureActivity.this).setTitle("HELP");

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
                            if(index == 2)
                            {
                                Intent intent = new Intent(getApplicationContext(),SetupActivity.class);
                                startActivityForResult(intent,SETUP);
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
                    AlertDialog.Builder builder  = new AlertDialog.Builder(CalibrationCaptureActivity.this).setTitle("RESOLUTION")
                            .setSingleChoiceItems(cameraSizeAdapter,SizeSelectIndex,new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String[] SizeTokenstrings = new String[2]; // SizeTokenstrings[0] = Camera.width  SizeTokenstrings[1] = Camera.height
                                    int cnt = 0;

                                    StringTokenizer string = new StringTokenizer(cameraSizeAdapter.getItem(which),"*");
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

                                        if ((preSize.width * cameraHeight) == (preSize.height * cameraWidth))

                                        {
                                            if(((preSize.width*9)==(preSize.height*16)) || ((preSize.width*3)==(preSize.height*4))) {

/*
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

*/

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
                    AlertDialog.Builder builder  = new AlertDialog.Builder(CalibrationCaptureActivity.this).setTitle("WHITE BALANCE")
                            .setSingleChoiceItems(WhiteBalanceAdapter,WBSelectIndex,new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int index){

                                    params.setWhiteBalance(WhiteBalanceAdapter.getItem(index));
                                    mCamera.setParameters(params);
                                    WBSelectIndex = index;
                                    Toast.makeText(getApplicationContext(), WhiteBalanceAdapter.getItem(index), Toast.LENGTH_SHORT).show();


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

            params = mCamera.getParameters();

            mCmcontrol = new CameraControl(this.getApplicationContext(),mCamera,params,dspWidth,dspHeight);

            // 지원하는 화이트밸런스 목록을 받아 저장
            WBitems = params.getSupportedWhiteBalance ();

            zoomSeekBar.setMax(params.getMaxZoom());

            // 촬영가능한 size 목록을 받아 저장
            supportedSize = params.getSupportedPictureSizes();
            previewSize = params.getSupportedPreviewSizes();
            Camera.Size maxCMsize = supportedSize.get(supportedSize.size()-1);

            cameraSizeList = new ArrayList<>();

            for(Camera.Size size : supportedSize)
            {
                if((size.width*3)==(size.height*4)) { //if((size.width*9)==(size.height*16) || (size.width*3)==(size.height*4)) {
                    cameraSizeList.add(String.valueOf(size.width) + "*" + String.valueOf(size.height));
                    if(maxCMsize.height<size.height)
                    {
                        maxCMsize = size;
                    }
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
                    params.setPictureSize(maxCMsize.width,maxCMsize.height);
                    watermark.setMaxWidth(dspWidth);
                    //watermark.setMaxHeight(custumScreensizeHeight);
                    watermark.setMinimumWidth(dspWidth);
                    //watermark.setMinimumHeight(custumScreensizeHeight);


                    break;
                }
            }


            params = mCmcontrol.initParameter();

            params.set("orientation", "portrait");
            params.set("rotation", 90);


            if(saveImageWidth!=0 && saveImageHeight!=0)
            {
                params.setPictureSize(saveImageWidth,saveImageHeight);
            }

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
        switch( v.getId() ) {
            case R.id.SHUTTERBTN :
                mCamera.takePicture(null, null, this);
                break;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        mCamera.startPreview();

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

        mCamera.release();

        sdRootPath = "/storage/sdcard0/DCIM/Magni";
        filePath = "/MAGNICapture_"+System.currentTimeMillis() +".jpg";

       // Bitmap rotateBitmap = mCmcontrol.imgaeRotate(data);

        Bitmap bitmap = null, rotateBitmap = null;

        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        rotateBitmap=null;

        if (bitmap.getWidth() > bitmap.getHeight()) {


            Matrix m = new Matrix();
            // matrix객체에 회전될 정보 입력
            m.postRotate(90);
            // 기존에 저장했던 bmp를 Matrix를 적용하여 다시 생성
            rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
        }

        // 이미지를 파일로 저장
       // File file = new File(filePath);
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "DCIM/Magni"+filePath);
        try {

            FileOutputStream fos = new FileOutputStream(file);
           // rotateBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            if(rotateBitmap != null) {
                rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            fos.write(data);
            fos.flush();
            fos.close();
            context.sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)) );

            Uri uirr = Uri.fromFile(file);

            Intent intent = new Intent(getApplicationContext(),CalibrationCaptureCheckActivity.class);
            Toast.makeText(getApplicationContext(),"저장완료 : "+filePath,Toast.LENGTH_SHORT).show();
            intent.putExtra("SavefilePath",filePath);
            intent.putExtra("UriSavefilePath",uirr.toString());
            releaseCamera();
            //startActivityForResult(intent, CALIB);
            setResult(RESULT_OK,intent);
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
        if((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP))
            mCamera.autoFocus(this);
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED,intent);
            finish();
        }
        return true;
    }
    /* @Override
     public void finish() {
         super.finish();
         super.onDestroy();
         mHolder = null;
         releaseCamera();
     }
 */
    private void releaseCamera(){


        int imageHeight = params.getPictureSize().height;
        int imageWidth = params.getPictureSize().width;
        int sizeIndex = this.SizeSelectIndex;

        editor.putInt("HEIGHT",imageHeight);
        editor.putInt("WIDTH",imageWidth);
        editor.putInt("SIZEINDEX",sizeIndex);
        editor.commit();

        if (mCamera != null) {
            mCamera.release();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Intent intent = new Intent();
        if(requestCode== SETUP)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
        else if(requestCode == CALIB)
        {
            switch (resultCode)
            {
                case RESULT_OK:



                    calibValue = data.getFloatExtra("CalibValue",0.0f);
//                    intent.putExtra("Height",params.getPictureSize().width);
 //                   intent.putExtra("Width",params.getPictureSize().height);
                   // intent.putExtra("CalibValue",calibValue);

                   // setResult(RESULT_OK,intent);
                    break;
                case RESULT_CANCELED:

                  //  setResult(RESULT_CANCELED,intent);
                    break;
            }
            returnSetup();
            setResult(RESULT_OK,intent2);
            releaseCamera();
            finish();
        }
    }

    void returnSetup()
    {
       intent2 = new Intent();
        intent2.putExtra("CALIBVALUE",calibValue);
    }

}
