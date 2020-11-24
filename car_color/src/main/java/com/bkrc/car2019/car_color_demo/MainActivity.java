package com.bkrc.car2019.car_color_demo;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ImageView show_view;
    private TextView wifi_ip, Camera_ip, r_num, g_num, blue_num, y_num, s_num, c_num, black_num;
    private EditText r_max, g_max, blue_max, y_max, s_max, c_max, black_max;
    private EditText r_min, g_min, blue_min, y_min, s_min, c_min, black_min;
    private TextView fruit_show;

    private String cameraIP;
    private Bitmap bitmap = null;
    private boolean flag = true;
    private WifiManager wifiManager;
    // 服务器管理器
    private DhcpInfo dhcpInfo;
    // 小车ip
    private String IPCar;
    private CameraCommandUtil cameraCommandUtil;
    // 广播名称
    public static final String A_S = "com.a_s";
    // 广播接收器
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent arg1) {
            cameraIP = arg1.getStringExtra("IP");
            progressDialog.dismiss();
            phThread.start();
            phHandler.sendEmptyMessage(30);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        wifi_Init();
    }

    private boolean wifi_flag = false;

    private void wifi_Init() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifi_flag = true;
            // 得到服务器的IP地址
            dhcpInfo = wifiManager.getDhcpInfo();
            IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);
            phHandler.sendEmptyMessage(20);
            // 注册广播
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(A_S);
            registerReceiver(myBroadcastReceiver, intentFilter);
            cameraCommandUtil = new CameraCommandUtil();
            search();
        } else {
            Toast.makeText(MainActivity.this, "请开启WIFI并重启应用", Toast.LENGTH_LONG).show();
        }

    }


    private void Init() {
        show_view = (ImageView) findViewById(R.id.show_view);
        wifi_ip = (TextView) findViewById(R.id.wifi_ip);
        Camera_ip = (TextView) findViewById(R.id.Camera_ip);
        fruit_show = (TextView) findViewById(R.id.fruit_show);

        r_num = (TextView) findViewById(R.id.r_num);
        g_num = (TextView) findViewById(R.id.g_num);
        blue_num = (TextView) findViewById(R.id.blue_num);
        y_num = (TextView) findViewById(R.id.y_num);
        s_num = (TextView) findViewById(R.id.s_num);
        c_num = (TextView) findViewById(R.id.c_num);
        black_num = (TextView) findViewById(R.id.black_num);

        r_max = (EditText) findViewById(R.id.r_max);
        g_max = (EditText) findViewById(R.id.g_max);
        blue_max = (EditText) findViewById(R.id.blue_max);
        y_max = (EditText) findViewById(R.id.y_max);
        s_max = (EditText) findViewById(R.id.s_max);
        c_max = (EditText) findViewById(R.id.c_max);
        black_max = (EditText) findViewById(R.id.black_max);

        r_min = (EditText) findViewById(R.id.r_min);
        g_min = (EditText) findViewById(R.id.g_min);
        blue_min = (EditText) findViewById(R.id.blue_min);
        y_min = (EditText) findViewById(R.id.y_min);
        s_min = (EditText) findViewById(R.id.s_min);
        c_min = (EditText) findViewById(R.id.c_min);
        black_min = (EditText) findViewById(R.id.black_min);
    }

    public void myonClick(View v) {
        if (v.getId() == R.id.start_dis) {
            if ((wifi_flag = true) && (IPCar != null) && (cameraIP != null) && (!cameraIP.equals(""))) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (bitmap != null) {
                            convertToBlack(bitmap);
                        } else {
                            Toast.makeText(MainActivity.this, "无摄像头图片" + "/n" + "请重启摄像头", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            } else {
                Toast.makeText(MainActivity.this, "未连接网络", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 搜索进度
    private ProgressDialog progressDialog = null;

    // 搜索摄像cameraIP进度条
    private void search() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在搜索摄像头");
        progressDialog.show();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SearchService.class);
        startService(intent);
    }

    // 开启线程接受摄像头当前图片
    private Thread phThread = new Thread(new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            Looper.prepare();
            while (true) {
                if (flag) {
                    bitmap = cameraCommandUtil.httpForImage(cameraIP);
                    phHandler.sendEmptyMessage(10);
                }
            }
        }
    });

    // 显示图片
    public Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                show_view.setImageBitmap(bitmap);
            }
            if (msg.what == 20) {
                wifi_ip.setText(IPCar);
            }
            if (msg.what == 30) {
                Camera_ip.setText(cameraIP);
            }
            if (msg.what == 40) {
                r_num.setText("" + colorNum[1]);
                g_num.setText("" + colorNum[2]);
                blue_num.setText("" + colorNum[3]);
                y_num.setText("" + colorNum[4]);
                s_num.setText("" + colorNum[5]);
                c_num.setText("" + colorNum[6]);
                black_num.setText("" + colorNum[7]);
                for (int i = 0; i < 8; i++) {
                    colorNum[i] = 0;
                }
            }
        }
    };


    private int red_max = 0, green_max = 0, blues_max = 0, yellow_max = 0, sort_max = 0, ching_max = 0, blacks_max = 0;
    private int red_min = 0, green_min = 0, blues_min = 0, yellow_min = 0, sort_min = 0, ching_min = 0, blacks_min = 0;

    private void get_input_threshold() {
        red_max = Integer.getInteger(r_max.getText().toString());
        green_max = Integer.getInteger(g_max.getText().toString());
        blues_max = Integer.getInteger(blue_max.getText().toString());
        yellow_max = Integer.getInteger(y_max.getText().toString());
        sort_max = Integer.getInteger(s_max.getText().toString());
        ching_max = Integer.getInteger(c_max.getText().toString());
        blacks_max = Integer.getInteger(black_max.getText().toString());

        red_min = Integer.getInteger(r_min.getText().toString());
        green_min = Integer.getInteger(g_min.getText().toString());
        blues_min = Integer.getInteger(blue_min.getText().toString());
        yellow_min = Integer.getInteger(y_min.getText().toString());
        sort_min = Integer.getInteger(s_min.getText().toString());
        ching_min = Integer.getInteger(c_min.getText().toString());
        blacks_min = Integer.getInteger(black_min.getText().toString());

    }


    private int[] colorNum = new int[8];//红、绿、蓝、黄、品、青、黑色个数
    private int blackMax = 255; //黑色最大RGB值和
    private int RGBMax = 365;   //红绿蓝最大RGB值和
    private int noiseMax = 510; //黄品青最大RGB值和

    private Bitmap convertToBlack(Bitmap bip) {// 像素处理背景变为白色，红、绿、蓝、黄、品、青、黑色，白色不变
        int width = bip.getWidth();
        int height = bip.getHeight();
        int[] pixels = new int[width * height];
        bip.getPixels(pixels, 0, width, 0, 0, width, height);       // 把二维图片的每一行像素颜色值读取到一个一维数组中
        int[] pl = new int[bip.getWidth() * bip.getHeight()];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                int rgb = r + g + b;
                if (rgb < blackMax)                //黑色
                {
                    pl[offset + x] = pixel;
                    colorNum[7]++;
                } else if (rgb < RGBMax) {        // 红绿蓝
                    pl[offset + x] = pixel;
                    if (r > g && r > b)
                        colorNum[1]++;            //红色
                    else if (g > b)
                        colorNum[2]++;            //绿色
                    else
                        colorNum[3]++;            //蓝色

                } else if (rgb < noiseMax) {      //黄、品和青
                    pl[offset + x] = pixel;
                    if (b < r && b < g)
                        colorNum[4]++;             //黄色
                    else if (g < r)
                        colorNum[5]++;             //品色
                    else
                        colorNum[6]++;            //青色
                } else {
                    pl[offset + x] = 0xffffffff;// 白色
                }
            }
        }
        phHandler.sendEmptyMessage(40);
        Bitmap result = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);//把颜色值重新赋给新建的图片 图片的宽高为以前图片的值
        result.setPixels(pl, 0, width, 0, 0, width, height);
        return result;
    }
}
