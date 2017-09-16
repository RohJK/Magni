package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;


public class IntroActivity extends FragmentActivity {

   // private String Intro = "";

    static String TAG = "IntroActivity";

    private Handler mHandle;

    final int MY_PERMISSION_REQUEST = 7;

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
       // requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        setContentView(R.layout.activity_main);


        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);


       /*String str = Environment.getExternalStorageState();
        if(str.equals(Environment.MEDIA_MOUNTED)){

            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST);
            }

            String dirPath = "/storage/sdcard0/DCIM/Magni";
            File file = new File(dirPath);
            if(!file.exists())
                file.mkdirs();
        }
        else{
            Toast.makeText(IntroActivity.this, "SD Card 인식 실패", Toast.LENGTH_SHORT).show();
        }*/


        if(Build.VERSION.SDK_INT >= 23 )
        {
            checkPermission();
        }
        else
        {
            String str = Environment.getExternalStorageState();
            if(str.equals(Environment.MEDIA_MOUNTED)){

                boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST);
                }

                String dirPath = "/storage/sdcard0/DCIM/Magni";
                File file = new File(dirPath);
                if(!file.exists())
                    file.mkdirs();
            }
            else{
                Toast.makeText(IntroActivity.this, "SD Card 인식 실패", Toast.LENGTH_SHORT).show();
            }
            Loading();
        }

    }

    private void Loading()
    {
        mHandle = new Handler()
        {
            public void handleMessage(Message msg)
            {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has not been granted.

                    requestCameraPermission();

                } else {
                    Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mHandle.sendEmptyMessageDelayed(0, 2500);
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean writeAccepted = false;
        switch(requestCode){
            case MY_PERMISSION_REQUEST:
                writeAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(writeAccepted) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/" + "DCIM/Magni");
                //String dirPath = "/storage/sdcard0/DCIM/Magni";
               // File file = new File(dirPath);
                boolean b = dir.mkdirs();
                if(b){
                    Log.i("TAG", "WOW! "+dir+" created!");
                }else{
                    Log.e("TAG", "OPS! "+dir+" NOT created! To be sure: new dir exist? "+dir.exists());
                }

                }
            }
*/


    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);

            // 다이얼로그로 처리해야함
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }


    private void requestStoragePermission() {
        Log.i(TAG, "STORAGE permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);

            // 다이얼로그로 처리해야함
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
        // END_INCLUDE(camera_permission_request)
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                //Snackbar.make(mLayout, R.string.permision_available_camera,
                //        Snackbar.LENGTH_SHORT).show();
                Loading();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
            }
            // END_INCLUDE(permission_result)

        }else if(requestCode == REQUEST_STORAGE)
        {
            Log.i(TAG, "Received response for Storage permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "Storage permission has now been granted. Showing preview.");
                //Snackbar.make(mLayout, R.string.permision_available_camera,
                //        Snackbar.LENGTH_SHORT).show();
                checkPermission();
            } else {
                Log.i(TAG, "Storage permission was NOT granted.");
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void checkPermission()
    {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestStoragePermission();

        } else {

            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/" + "DCIM/Magni");
                //String dirPath = "/storage/sdcard0/DCIM/Magni";
                // File file = new File(dirPath);
                boolean b = dir.mkdirs();
                if(b){
                    Log.i("TAG", "WOW! "+dir+" created!");
                }else{
                    Log.e("TAG", "OPS! "+dir+" NOT created! To be sure: new dir exist? "+dir.exists());
                }

            }

            Loading();
        }
    }

}
