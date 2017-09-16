package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class SetupActivity extends Activity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Button calibrationBtn;
    Button okBtn;
    //Button cancelBtn;

    CalibrationValueSave calibrationValueSave;

    int calibImageHeight;
    int calibImageWidth;
    float nanometerPerPixel;
    String nanometerPerPixelString;

    Uri uri;

    TextView heightView;
    TextView widthView;
    TextView calibValueView;

    final int CalibREturn = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상단바 제거
        setContentView(R.layout.activity_setup);
        pref = getSharedPreferences("CalibDataSAVE",MODE_PRIVATE);
        editor = pref.edit();

        nanometerPerPixel = pref.getFloat("CalibValue",0);
        calibImageHeight = pref.getInt("HEIGHT",0);
        calibImageWidth = pref.getInt("WIDTH",0);

        calibrationValueSave = new CalibrationValueSave();

        calibValueView = (TextView)findViewById(R.id.tx3);
        widthView = (TextView)findViewById(R.id.tx2);
        heightView = (TextView)findViewById(R.id.tx1);

        calibValueView.setText("Calibration Value : " + String.valueOf(nanometerPerPixel));
        widthView.setText(" Width : " + String.valueOf(calibImageWidth));
        heightView.setText(" Height : "+String.valueOf(calibImageHeight));



        calibrationBtn = (Button)findViewById(R.id.CALIBRATIONButton);
        okBtn = (Button)findViewById(R.id.SETUPOKButton);
        //cancelBtn = (Button)findViewById(R.id.SETUPCANCELButton);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("CalibValue",String.valueOf(nanometerPerPixel));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
      /*  cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });*/
        calibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CalibrationCaptureCheckActivity.class);
                startActivityForResult(intent,CalibREturn);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CalibREturn)
        {
            switch (resultCode)
            {
                case RESULT_OK:


                     calibImageHeight = data.getIntExtra("Height",0);
                     calibImageWidth = data.getIntExtra("Width",0);
                    nanometerPerPixelString = data.getStringExtra("CalibValue");
                    nanometerPerPixel = Float.valueOf(nanometerPerPixelString);

                    calibValueView.setText("Calibration Value : " + String.valueOf(nanometerPerPixel));
                    widthView.setText(" Width : " + String.valueOf(calibImageWidth));
                    heightView.setText(" Height : "+String.valueOf(calibImageHeight));

                    break;

                case RESULT_CANCELED:



                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("CalibValue",nanometerPerPixelString);
        setResult(RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        editor.putInt("HEIGHT",calibImageHeight);
        editor.putInt("WIDTH",calibImageWidth);
        editor.putFloat("CalibValue",nanometerPerPixel);
        editor.commit();

    }
}
