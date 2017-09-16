package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by RohJK on 2015-05-22.
 */
public class CalibrationPointView extends View {
    public float x1= 0, y1= 0;
    public float x2= 0, y2= 0;

    Bitmap redCircle = BitmapFactory.decodeResource(getResources(), R.drawable.magnicircle_red101);
    Bitmap blueCircle = BitmapFactory.decodeResource(getResources(),R.drawable.magnicircle_blue101);
    Bitmap resizeRedCirdle;
    Bitmap resizeBlueCircle;



    public float calibValue;

    public int state;

    public CalibrationPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resizeRedCirdle = Bitmap.createScaledBitmap(redCircle,101,101,true);
        resizeBlueCircle = Bitmap.createScaledBitmap(blueCircle,101,101,true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint= new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        // canvas.drawRect(x1, y1, x1+ 50, y1+ 50, paint);
        //canvas.drawCircle(x1,y1,25f,paint);
        canvas.drawBitmap(resizeBlueCircle,x1,y1,paint);
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        // canvas.drawRect(x2, y2, x2+ 50, y2+ 50, paint);
        //canvas.drawCircle(x2,y1,25f,paint);
        canvas.drawBitmap(resizeRedCirdle,x2,y1,paint);
        if((x1!=0||y1!=0)&&(x2!=0||y2!=0))
        {
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2.5f);
            //canvas.drawText();
            canvas.drawLine(x1 + 50, y1 + 50, x2 + 50, y1 + 50,paint);
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

    public float getPixel()
    {
        return Math.abs(x1-x2);
    }

    public void init()
    {
        x1= 0; y1= 0;
        x2= 0; y2= 0;
        state=0;
    }

}
