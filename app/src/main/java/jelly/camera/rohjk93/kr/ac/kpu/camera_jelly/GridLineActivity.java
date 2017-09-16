package jelly.camera.rohjk93.kr.ac.kpu.camera_jelly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


public class GridLineActivity extends Activity {

    Button ok;
    EditText x,y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상단바 제거
        setContentView(R.layout.activity_grid_line);

        x = (EditText)findViewById(R.id.XEdit);
        y = (EditText)findViewById(R.id.YEdit);

        ok = (Button)findViewById(R.id.gridOK);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("X",x.getText().toString());
                intent.putExtra("Y",y.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

}
