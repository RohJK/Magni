package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.util.List;

/**
 * Created by RohJK on 2015-04-03.
 */
public class CameraControl implements SurfaceHolder.Callback , Camera.AutoFocusCallback, Camera.PictureCallback {


    Context context;
    Camera camera;
    Camera.Parameters parameters;

     List< String > WBitems;
     List<Camera.Size> previewSize;
     List<Camera.Size> supportedSize;
    int dspWidth,dspHeight;

    CameraControl()
    {

    }

    CameraControl(Context context, Camera camera, Camera.Parameters parameters, int dspWidth, int dspHeight)
    {
        this.context = context;
        this.camera = camera;
        this.parameters = parameters;
        this.dspHeight = dspHeight;
        this.dspWidth = dspWidth;


        previewSize = parameters.getSupportedPreviewSizes();
        supportedSize = parameters.getSupportedPictureSizes();



    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }


    public Bitmap imgaeRotate(byte[] data)
    {
/*
        // data[] 로 넘어온 데이터를 bitmap으로 변환
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        // 화면 회전을 위한 matrix객체 생성
        Matrix m = new Matrix();
        // matrix객체에 회전될 정보 입력
        m.postRotate(90);
        // 기존에 저장했던 bmp를 Matrix를 적용하여 다시 생성
        Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
        // 기존에 생성했던 bmp 자원해제
        bmp.recycle();

        return rotateBitmap;
        */
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bmp;
    }


    public Camera.Parameters initParameter()
    {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        parameters.setWhiteBalance("auto");
        parameters.setPictureFormat(ImageFormat.JPEG); // set JPEG
        parameters.setJpegQuality(100);

        return parameters;
    }



}
