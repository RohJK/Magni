package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageLoadLengthMeasurementActivity extends Activity {

    final int GRID =111;
    final int GalleryIMAGEPATH = 33;

    RelativeLayout layout;
    boolean seekBarState = false;

    Bitmap saveSkinImage;
    Intent intent;
    Uri uri;

    private ImageView saveimageView;
    PhotoViewAttacher mAttacher;

    private int imageWidth,imageHeight;
    private int dspWidth,dspHeight,downimageHeight,downimageWidth;

    private Button okButton;
    Button lengthButton;
    //Button getImageBtn;


    PointView pointView;
    Button p1,p2,line,lineok,gridBtn;
    LinearLayout linearLayout;
    TextView lineLengthTextview;
    SeekBar seekBar;


    String savefilepath;

    String calibValueString;
    int zoomValue;

    float scaleValue;
    float nanoPerPx;//  = 9.4978310f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상단바 제거
        setContentView(R.layout.activity_image_load_length_measurement);
        layout = (RelativeLayout)findViewById(R.id.LoadcaptureLayout);
/*
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
*/

        intent = getIntent();
        /////////////////////////////////////////////////////////////////////////이미지 로드 ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        int calibValueState = intent.getIntExtra("STATE",0);
        if(calibValueState == 0) {
            calibValueString = intent.getStringExtra("CalibValue");
            nanoPerPx = Float.valueOf(calibValueString);
        }


        saveimageView = (ImageView)findViewById(R.id.SaveImageView);
        mAttacher = new PhotoViewAttacher(saveimageView);
        saveimageView.setImageBitmap(saveSkinImage);
        /////////////////////////////////////////////////////////////////////////이미지 로드 ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        okButton = (Button)findViewById(R.id.OKBUTTON);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                startActivity(intent);
               /* saveSkinImage.recycle();
                saveSkinImage = null;*/
                finish();
            }
        });
/*
        getImageBtn = (Button)findViewById(R.id.GetCalibImagebutton);
        getImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GalleryIMAGEPATH);
            }
        });
        */

        lengthButton = (Button)findViewById(R.id.Lengthbutton);
        lengthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nanoPerPx!=0 && zoomValue == 0) {
                    scaleValue = mAttacher.getScale();
                    pointView.scaleValue = scaleValue;
                    lengthButton.setVisibility(View.INVISIBLE);
                    okButton.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    pointView.setVisibility(View.VISIBLE);
                    if(pointView.grid == true)
                    {
                        seekBar.setVisibility(View.VISIBLE);
                    }

                }else{
                    if(nanoPerPx==0) {
                         Toast.makeText(getApplicationContext(), "길이측정을 위한 Calibration 데이터가 없습니다", Toast.LENGTH_SHORT).show();
                         Toast.makeText(getApplicationContext(), "설정에서 Calibration 해 주세요", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        /////////////////////////////////////////////////////////////////////////PointView ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        pointView = (PointView)findViewById(R.id.Pointview);
        linearLayout = (LinearLayout)findViewById(R.id.PointLinear);
        p1 = (Button)findViewById(R.id.X1Button);
        p2 = (Button)findViewById(R.id.X2Button);
        line = (Button)findViewById(R.id.LineButton);
        lineok = (Button)findViewById(R.id.LineOKButton);
        lineLengthTextview = (TextView)findViewById(R.id.LineLengthTextView);
        gridBtn = (Button)findViewById(R.id.LineGridButton);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        // pointView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        pointView.calibValue = nanoPerPx;
        // pointView.dspHeight =  pointView.getMeasuredHeight();
        //pointView.dspWidth =  pointView.getMeasuredWidth();
        // pointView.dspX = pointView.getX();
        // pointView.dspY = pointView.getY();


        seekBar.setMax(20);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pointView.gridY = progress+2;
                pointView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pointView.grid == false)
                {
                    Toast.makeText(getApplicationContext(),"GRID ON",Toast.LENGTH_SHORT).show();
                    pointView.dspHeight =  pointView.getMeasuredHeight();
                    pointView.dspWidth =  pointView.getMeasuredWidth();
                    // pointView.invalidate();
                    pointView.grid = true;
                    seekBar.setVisibility(View.VISIBLE);
                    pointView.invalidate();
                    gridBtn.setBackgroundColor(Color.rgb(236,236,236));
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"GRID OFF",Toast.LENGTH_SHORT).show();
                    pointView.grid = false;
                    seekBar.setVisibility(View.INVISIBLE);
                    pointView.invalidate();
                    gridBtn.setBackgroundColor(Color.rgb(255, 255, 255));
                }

            }
        });

        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointView.state = 1;
                p1.setBackgroundColor(Color.rgb(236, 236, 236));
                p2.setBackgroundColor(Color.rgb(255,255,255));
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointView.state = 2;
                p1.setBackgroundColor(Color.rgb(255,255,255));
                p2.setBackgroundColor(Color.rgb(236,236,236));
            }
        });

        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                lineLengthTextview.setText(" ");
                lineLengthTextview.setVisibility(View.VISIBLE);

                scaleValue = mAttacher.getScale();
                float mmLength = (nanoPerPx * pointView.getLineLentgth())/1000.0f/scaleValue;
                //lineLengthTextview.setText(String.valueOf(pointView.getLineLentgth()));
                String s = String.format("%.3f", mmLength);
                lineLengthTextview.setText(s+" mm");

*/               pointView.dspHeight =  pointView.getMeasuredHeight();
                pointView.dspWidth =  pointView.getMeasuredWidth();

                linearLayout.setVisibility(View.INVISIBLE);
                if(seekBar.getVisibility() == View.VISIBLE)
                {
                    seekBar.setVisibility(View.INVISIBLE);
                    seekBarState = true;
                }

                layout.buildDrawingCache();
                Bitmap captureView = Bitmap.createBitmap(pointView.dspWidth, pointView.dspHeight, Bitmap.Config.ARGB_8888);
                captureView = layout.getDrawingCache();
                FileOutputStream fos;

                String sdRootPath = "/storage/sdcard0/DCIM/Magni";
                String filePath = "/MAGNICapture_"+System.currentTimeMillis() +".jpg";
                // File file = new File(filePath);
                File file = new File(Environment.getExternalStorageDirectory() + "/" + "DCIM/Magni"+filePath);
                try {
                    fos = new FileOutputStream(file);
                    captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    ImageLoadLengthMeasurementActivity.this.sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)) );

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if(seekBarState)
                {
                    seekBar.setVisibility(View.VISIBLE);
                    seekBarState = false;
                }
                linearLayout.setVisibility(View.VISIBLE);

                // Toast.makeText(getApplicationContext(), filePath + " saved",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"save : "+filePath,Toast.LENGTH_SHORT).show();

            }
        });


        lineok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                pointView.setVisibility(View.INVISIBLE);
                okButton.setVisibility(View.VISIBLE);
                lengthButton.setVisibility(View.VISIBLE);
                lineLengthTextview.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.INVISIBLE);
                pointView.init();
            }
        });


        /////////////////////////////////////////////////////////////////////////PointView ////////////////////////////////////////////////////////////////////////////////////////////////////////////



        getImage();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GRID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    pointView.gridX = Integer.valueOf(data.getStringExtra("X"));
                    pointView.gridY = Integer.valueOf(data.getStringExtra("Y"));
                    pointView.invalidate();
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
        if(requestCode == GalleryIMAGEPATH)
        {
            switch (resultCode)
            {
                case RESULT_OK:


                    //getImageBtn.setVisibility(View.INVISIBLE);

                    // 경로를 받고
                    uri = data.getData();
                    savefilepath = uri.toString();

                    try {
                        saveSkinImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageWidth = saveSkinImage.getWidth();
                    imageHeight = saveSkinImage.getHeight();

//                    Log.e(tag,String.valueOf(mAttacher.getScale()));

                    saveimageView = (ImageView)findViewById(R.id.SaveImageView);
                    mAttacher = new PhotoViewAttacher(saveimageView);

                    saveimageView.setImageBitmap(saveSkinImage);



                    break;

                case RESULT_CANCELED:
                    Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                    startActivity(intent);
                   // saveSkinImage.recycle();
                    //saveSkinImage = null;
                    finish();
                    break;

            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
        startActivity(intent);
        //saveSkinImage.recycle();
        //saveSkinImage = null;
        finish();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {

        if(saveSkinImage != null)
        {
            saveSkinImage.recycle();
            saveSkinImage = null;
        }

        super.onDestroy();
    }

    public void getImage()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GalleryIMAGEPATH);
    }
}
