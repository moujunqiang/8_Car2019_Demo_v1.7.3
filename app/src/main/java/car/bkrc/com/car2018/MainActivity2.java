package car.bkrc.com.car2018;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import car.bkrc.com.car2018.car_shape_demo.CarShapeDemoActivity;
import car.bkrc.com.car2018.car_tesseracttest_custom_demo.CarTesseracttestActivity;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    /**
     * Car8
     */
    private Button mBtn1;
    /**
     * Car_color
     */
    private Button mBtn2;
    /**
     * Car_Shape
     */
    private Button mBtn3;
    /**
     * Car_tesseracttest
     */
    private Button mBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
    }

    private void initView() {
        mBtn1 = (Button) findViewById(R.id.btn_1);
        mBtn1.setOnClickListener(this);
        mBtn2 = (Button) findViewById(R.id.btn_2);
        mBtn2.setOnClickListener(this);
        mBtn3 = (Button) findViewById(R.id.btn_3);
        mBtn3.setOnClickListener(this);
        mBtn4 = (Button) findViewById(R.id.btn_4);
        mBtn4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_1:
                startActivity(new Intent(MainActivity2.this,LoginActivity.class));
                break;
            case R.id.btn_2:
                startActivity(new Intent(MainActivity2.this,MainActivity.class));

                break;
            case R.id.btn_3:
                startActivity(new Intent(MainActivity2.this, CarShapeDemoActivity.class));

                break;
            case R.id.btn_4:
                startActivity(new Intent(MainActivity2.this, CarTesseracttestActivity.class));

                break;
        }
    }
}