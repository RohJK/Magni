package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;


public class CalibrationCaptureCheckActivity extends Activity {

    String tag = "TEG";

    Bitmap saveSkinImage;
    Intent intent;
    Uri uri;

    String savefilepath;

    private ImageView saveimageView;
    PhotoViewAttacher mAttacher;

    private int imageWidth,imageHeight;

    private Button okButton,cancelButton;
    Button calibrationButton;
    Button setImageBtn;
    Button getImageBtn;

    final int IMAGEPATH = 33;
    final int GalleryIMAGEPATH = 22;


    CalibrationPointView calibrationPointView;
    Button p1,p2,line,lineok;
    LinearLayout linearLayout;
    TextView lineLengthTextview;

    float scaleValue;
    float nanoPerPx; //= 5000.0f/532.0f;
    double doubleNamnoPerpx;
    float returnPixel;
    String nanoPerPxString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상단바 제거
        setContentView(R.layout.activity_calibration_capture_check);

        setImageBtn = (Button)findViewById(R.id.SetCalibImageButton);
        setImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(getApplicationContext(),CalibrationCaptureActivity.class);
                startActivityForResult(captureIntent,IMAGEPATH);
            }
        });
        getImageBtn = (Button)findViewById(R.id.GetCalibImagebutton);
        getImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,GalleryIMAGEPATH);
            }
        });



   /*     intent = getIntent();
        /////////////////////////////////////////////////////////////////////////이미지 로드 ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        String savefilepath = intent.getExtras().getString("UriSavefilePath");//"/storage/sdcard0/DCIM/SkinCapture.jpg";
        uri = Uri.parse(savefilepath);

        try {
            saveSkinImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageWidth = saveSkinImage.getWidth();
        imageHeight = saveSkinImage.getHeight();

        saveimageView = (ImageView)findViewById(R.id.SaveImageView);
        mAttacher = new PhotoViewAttacher(saveimageView);
        saveimageView.setImageBitmap(saveSkinImage);
     */

        //saveimageView = (ImageView)findViewById(R.id.SaveImageView);
        //mAttacher = new PhotoViewAttacher(saveimageView);
        //saveimageView.setImageBitmap(saveSkinImage);

         /////////////////////////////////////////////////////////////////////////이미지 로드 ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        okButton = (Button)findViewById(R.id.OKBUTTON);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.putExtra("Height",imageHeight);
                intent.putExtra("Width",imageWidth);
                intent.putExtra("CalibValue",nanoPerPxString);

                setResult(RESULT_OK,intent);

                if(nanoPerPx == 0)
                {
                    setResult(RESULT_CANCELED,intent);
                }

               /* if(savefilepath != null) {
                    saveSkinImage.recycle();
                    saveSkinImage = null;
                }*/
                finish();
            }
        });

        cancelButton = (Button)findViewById(R.id.CANCELbutton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
               /* if(savefilepath != null) {
                    saveSkinImage.recycle();
                    saveSkinImage = null;
                }*/
                finish();
            }
        });



        calibrationButton = (Button)findViewById(R.id.CalibRAtionButton);
        calibrationButton.setVisibility(View.INVISIBLE);
        calibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savefilepath!=null) {
                    calibrationButton.setVisibility(View.INVISIBLE);
                    okButton.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    calibrationPointView.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"사진을 추가해주세요",Toast.LENGTH_SHORT).show();
                }

            }
        });

        /////////////////////////////////////////////////////////////////////////PointView ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        calibrationPointView = (CalibrationPointView)findViewById(R.id.CalibPointview);
        linearLayout = (LinearLayout)findViewById(R.id.PointLinear);
        p1 = (Button)findViewById(R.id.X1Button);
        p2 = (Button)findViewById(R.id.X2Button);
        line = (Button)findViewById(R.id.LineButton);
        lineok = (Button)findViewById(R.id.LineOKButton);
        lineLengthTextview = (TextView)findViewById(R.id.LineLengthTextView);

        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrationPointView.state = 1;
                p1.setBackgroundColor(Color.rgb(236, 236, 236));
                p2.setBackgroundColor(Color.rgb(255,255,255));
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrationPointView.state = 2;
                p1.setBackgroundColor(Color.rgb(255,255,255));
                p2.setBackgroundColor(Color.rgb(236,236,236));
            }
        });

        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lineLengthTextview.setText(" ");
                lineLengthTextview.setVisibility(View.VISIBLE);

                scaleValue = mAttacher.getScale();
                //float mmLength = (nanoPerPx * calibrationPointView.getLineLentgth())/1000.0f/scaleValue;
                returnPixel = calibrationPointView.getPixel();
                //lineLengthTextview.setText(String.valueOf(pointView.getLineLentgth()));
                lineLengthTextview.setText(String.valueOf(returnPixel)+" Pixel");

            }
        });


        lineok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                calibrationPointView.setVisibility(View.INVISIBLE);
                okButton.setVisibility(View.VISIBLE);
                calibrationButton.setVisibility(View.VISIBLE);
                lineLengthTextview.setVisibility(View.INVISIBLE);
                returnPixel = calibrationPointView.getPixel();
                if(returnPixel!=0.0f && calibrationPointView.x1 != 0.0f && calibrationPointView.x2 != 0.0f && calibrationPointView.y1 != 0.0f && calibrationPointView.y2 != 0.0f) {
                    //String s = String.format("%.4f", pixel);
                    //String s = String.valueOf(pixel);
                    // pixel = Float.valueOf(s);
                    //doubleNamnoPerpx = Double.valueOf(strNumber);
                    scaleValue = mAttacher.getScale();
                    nanoPerPx = (5000.0f / returnPixel) * scaleValue;
                    //nanoPerPxString = String.format("%.4f", nanoPerPx);
                    nanoPerPxString = String.valueOf(nanoPerPx);
                    //doubleNamnoPerpx = Double.valueOf(strNumber);
                }
                calibrationPointView.init();
            }
        });


        /////////////////////////////////////////////////////////////////////////PointView //////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGEPATH)
        {
            switch (resultCode)
            {
                case RESULT_OK:

                    setImageBtn.setVisibility(View.INVISIBLE);
                    getImageBtn.setVisibility(View.INVISIBLE);

                    // 경로를 받고
                    savefilepath = data.getStringExtra("UriSavefilePath"); //"/storage/sdcard0/DCIM/SkinCapture.jpg";
                    uri = Uri.parse(savefilepath);

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

                    calibrationButton.setVisibility(View.VISIBLE);

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

                    setImageBtn.setVisibility(View.INVISIBLE);
                    getImageBtn.setVisibility(View.INVISIBLE);

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

                    calibrationButton.setVisibility(View.VISIBLE);

                    break;

                case RESULT_CANCELED:
                    break;

            }
        }

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
       /* if(savefilepath != null) {
            saveSkinImage.recycle();
            saveSkinImage = null;
        }*/
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if(savefilepath != null) {
            saveSkinImage.recycle();
            saveSkinImage = null;
        }

        super.onDestroy();
    }
}
