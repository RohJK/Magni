package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by RohJK on 2015-05-21.
 */
public class PointView extends View {
    public float x1= 0, y1= 0;
    public float x2= 0, y2= 0;

    public float calibValue,scaleValue;

    public int state;
    public boolean grid = false;
    public int gridX=2, gridY=2;
    int xcount=0,ycount=0;
    public int dspHeight, dspWidth, dspX,dspY;

    int angle;

   // Bitmap redCircle = BitmapFactory.decodeResource(getResources(),R.drawable.magnicircle_red51);
    //Bitmap blueCircle = BitmapFactory.decodeResource(getResources(),R.drawable.magnicircle_blue51);
    Bitmap redCircle = BitmapFactory.decodeResource(getResources(),R.drawable.magnicircle_red101);
    Bitmap blueCircle = BitmapFactory.decodeResource(getResources(),R.drawable.magnicircle_blue101);
    Bitmap resizeRedCirdle;
    Bitmap resizeBlueCircle;

    Bitmap angleRed;
    Bitmap angleBlue;

  //  Matrix m = new Matrix();

    //int circleWidth = 51, circleHeight = 51;
    int circleWidth = 101, circleHeight = 101;
    int imageWidth,imageHeight;

    // SJ : 회전시 이미지 크기가 변하기 때문에 원점의 위치를 바꿔주기위한 크기 차이(Gap)
    int nRotateGap=0;



    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //m.postScale(circleWidth, circleHeight);
        imageWidth = redCircle.getWidth();
        imageHeight = redCircle.getHeight();
       // resizeRedCirdle = Bitmap.createScaledBitmap(redCircle, 51, 51, true);
       // resizeBlueCircle = Bitmap.createScaledBitmap(blueCircle, 51, 51, true);
         resizeRedCirdle = Bitmap.createScaledBitmap(redCircle,101,101,true);
         resizeBlueCircle = Bitmap.createScaledBitmap(blueCircle,101,101,true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint= new Paint();
        Paint prePaint= new Paint();

        int i=0;


        paint.setColor(Color.DKGRAY);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(95);

        //m.postRotate((float) (90.0f - angle));


         if(x1-x2<=0) {
               angleRed = rotate(resizeRedCirdle, 90 - (int) angle, 1);
               angleBlue = rotate(resizeBlueCircle, 90 - (int) angle,0);



         }else
         {
             angleRed = rotate(resizeRedCirdle, 90 + (int) angle,1);
             angleBlue = rotate(resizeBlueCircle, 90 + (int) angle,0);
         }

        if (grid == true)
        {

            for(i=0; i<gridY+1; i++)
            {
                canvas.drawLine(i * (dspWidth) / gridY , 0, i * (dspWidth) / gridY, dspHeight,paint);
            }
            for(i =0; i<=dspHeight; i+=(dspWidth) / gridY)
            {
                canvas.drawLine(0, i ,dspWidth, i,paint);
            }
            paint.setPathEffect(null);
        }


        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
       // canvas.drawRect(x1, y1, x1+ 50, y1+ 50, paint);
        //canvas.drawCircle(x1,y1,25f,paint);
       // paint.setColor(Color.GREEN);\
        // SJ : 이미지의 시작 위치를 구한 차이만큼 이동시킴
        canvas.drawBitmap(angleBlue,x1-nRotateGap,y1-nRotateGap,paint);
        paint.setAntiAlias(true);
       // canvas.drawRect(x2, y2, x2+ 50, y2+ 50, paint);
       // canvas.drawCircle(x2,y2,25f,paint);
        // SJ : 이미지의 시작 위치를 구한 차이만큼 이동시킴
        canvas.drawBitmap(angleRed,x2-nRotateGap,y2-nRotateGap,paint);
        if((x1!=0||y1!=0)&&(x2!=0||y2!=0)) {
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2.5f);
           // canvas.drawLine(x1 + (imageHeight/2), y1 + (imageHeight/2), x2 + (imageHeight/2), y2 + (imageHeight/2), paint);
            //canvas.drawLine(x1 + 25, y1 + 25, x2+ 25, y2 + 25, paint);
            canvas.drawLine(x1 + 50, y1 + 50, x2+ 50, y2 + 50, paint);

            angle = (int)getAngle(x1,y1,x2,y2);
            angle = Math.abs(angle);

            float length = getLength();
            String s = String.format("%.3f", length);
            paint.setTextSize(50);


           // canvas.drawText(String.valueOf(angle), (x1 + x2) / 2.0f, (y1 + y2) / 2.0f + 150.0f, paint);

            if(angle>=45 && angle<135)
            {
                if((y1+y2)/2.0f-200.0f<=0)
                {
                    canvas.drawText(s+" mm",(x1+x2)/2.0f,(y1+y2)/2.0f+150.0f,paint);
                }else {
                    canvas.drawText(s + " mm", (x1 + x2) / 2.0f, (y1 + y2) / 2.0f - 200.0f, paint);
                }
            }else if(angle<45 || angle>=135)
            {
                if((x1+x2)/2.0f-260.0f<=0) {
                    canvas.drawText(s + " mm", (x1 + x2) / 2.0f + 150.0f, (y1 + y2) / 2.0f, paint);
                }else
                {
                    canvas.drawText(s + " mm", (x1 + x2) / 2.0f - 260.0f, (y1 + y2) / 2.0f, paint);
                }
            }


             //angleRed.recycle();
             //angleBlue.recycle();

        }




    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(state==1) {
            x1 =  event.getX();
            y1 =  event.getY()-200.0f;
        }else if(state ==2)
        {
            x2 =  event.getX();
            y2 =  event.getY()-200.0f;
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {

        }

        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {

        }

        if(event.getAction() == MotionEvent.ACTION_UP)
        {

        }
        invalidate();
        return true;
    }

    public float getLineLentgth()
    {
        return (float)Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2));
    }

    public void init()
    {
        x1= 0; y1= 0;
        x2= 0; y2= 0;
        state=0;
    }

    private static double getAngle(float x1,float y1, float x2,float y2){

        int dx = (int)(x2 - x1);

        int dy = (int)(y2 - y1);



        double rad= Math.atan2(dx, dy);

        double degree = (rad*180)/Math.PI ;



        return degree;

    }

    private float getLength()
    {
        return (calibValue * getLineLentgth())/1000.0f/scaleValue;
    }


    public Bitmap rotate(Bitmap bitmap, int degrees, int nType) {
        if (degrees != 0 && bitmap != null) {

            // 이미지 회전하기 전의 크기
            int nBmpWidth = bitmap.getWidth();

            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            Log.d("pre - w, h : ",(float) bitmap.getWidth() / 2 + "  " +(float) bitmap.getHeight() / 2);

            try {
               // Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                   // bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // �޸𸮰� �����Ͽ� ȸ���� ��Ű�� ���� ��� �׳� ������ ��ȯ�մϴ�.
            }

                // SJ : 이미지 회전 후 커진 크기와 이미지 회전하기 전의 크기의 차를 구한 후 2로 나눔(너비이기 때문)
                nRotateGap = (bitmap.getWidth()-nBmpWidth)/2;

        }
        return bitmap;

    }


}
