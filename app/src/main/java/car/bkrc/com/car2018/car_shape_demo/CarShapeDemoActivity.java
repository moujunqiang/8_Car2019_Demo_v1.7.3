package car.bkrc.com.car2018.car_shape_demo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import car.bkrc.com.car2018.R;
import car.bkrc.com.car2018.car_shape_demo.Utils.Coordinates;

public class CarShapeDemoActivity extends Activity {

	private ImageView show_view,diap_view;
	private TextView wifi_ip,Camera_ip,r_num,g_num,blue_num;
	private EditText r_max,g_max,blue_max;
	private EditText r_min,g_min,blue_min;
	private TextView fruit_show;
	private CheckBox CheckBox_r,CheckBox_g,CheckBox_b;


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
		setContentView(R.layout.activity_car_shape_demo);
        XXPermissions.with(this).permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {

            }

            @Override
            public void noPermission(List<String> denied, boolean never) {

            }
        });
		Init();
		wifi_Init();
	}

	private boolean wifi_flag = false;
	private void wifi_Init()
	{
		wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if(wifiManager.isWifiEnabled())
		{
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
		}	else {

			Toast.makeText(CarShapeDemoActivity.this, "请开启WIFI并重启应用", Toast.LENGTH_LONG).show();
		}
	}


	private void Init()
	{
		show_view = (ImageView)findViewById(R.id.show_view);
		diap_view = (ImageView)findViewById(R.id.diap_view);
		show_view.setOnTouchListener(new ontouchlistener());

		wifi_ip = (TextView)findViewById(R.id.wifi_ip);
		Camera_ip = (TextView)findViewById(R.id.Camera_ip);
		fruit_show = (TextView)findViewById(R.id.fruit_show);

		r_num = (TextView)findViewById(R.id.r_num);
		g_num = (TextView)findViewById(R.id.g_num);
		blue_num = (TextView)findViewById(R.id.blue_num);

		r_max = (EditText)findViewById(R.id.r_max);
		g_max = (EditText)findViewById(R.id.g_max);
		blue_max = (EditText)findViewById(R.id.blue_max);

		r_min = (EditText)findViewById(R.id.r_min);
		g_min = (EditText)findViewById(R.id.g_min);
		blue_min = (EditText)findViewById(R.id.blue_min);

		CheckBox_r = (CheckBox)findViewById(R.id.checkBox_r);
		CheckBox_g = (CheckBox)findViewById(R.id.CheckBox_g);
		CheckBox_b = (CheckBox)findViewById(R.id.CheckBox_b);

		CheckBox_r.setOnCheckedChangeListener(new checkBox_onclick());
		CheckBox_g.setOnCheckedChangeListener(new checkBox_onclick());
		CheckBox_b.setOnCheckedChangeListener(new checkBox_onclick());
	}

	private int red_r = 0, green_g = 0, blue_b = 0;
	private class ontouchlistener implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			int x = (int) event.getX();
			int y = (int) event.getY();
			int pixel = bitmap.getPixel(x, y);
			red_r = (pixel & 0xff0000) >> 16;
			green_g = (pixel & 0xff00) >> 8;
			blue_b = (pixel & 0xff);
			phHandler.sendEmptyMessage(40);
			return false;
		}
	}


	private Bitmap bitmaps;
	//单选对话框监听类
	private class checkBox_onclick implements
			OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			// TODO 自动生成的方法存根
			if(buttonView.getId() == R.id.checkBox_r)
			{
				if(isChecked == true)
				{
					System.out.println("红色");
					CheckBox_g.setChecked(false);
					CheckBox_b.setChecked(false);
					get_input_threshold();
//				    InputStream is = getResources().openRawResource(R.drawable.o_red);
//				    Bitmap mBitmap = BitmapFactory.decodeStream(is);

					bitmaps = convertToBlack(bitmap,1);
					diap_view.setImageBitmap(bitmaps);

				} else {
					System.out.println("1");
				}
			}
			if(buttonView.getId() == R.id.CheckBox_g)
			{
				if(isChecked == true)
				{
					System.out.println("绿色");
					CheckBox_r.setChecked(false);
					CheckBox_b.setChecked(false);
					get_input_threshold();
//					InputStream is = getResources().openRawResource(R.drawable.c_red);
//				    Bitmap mBitmap = BitmapFactory.decodeStream(is);
					bitmaps = convertToBlack(bitmap,2);
					diap_view.setImageBitmap(bitmaps);
				} else {
					System.out.println("2");
				}
			}
			if(buttonView.getId() == R.id.CheckBox_b)
			{
				if(isChecked == true)
				{
					System.out.println("蓝色");
					CheckBox_r.setChecked(false);
					CheckBox_g.setChecked(false);
					get_input_threshold();
//				    InputStream is = getResources().openRawResource(R.drawable.red_s);
//				    Bitmap mBitmap = BitmapFactory.decodeStream(is);
					bitmaps = convertToBlack(bitmap,3);
					diap_view.setImageBitmap(bitmaps);
				} else {
					System.out.println("3");
				}
			}
		}
	}

	public void myonClick(View v)
	{
		if(v.getId() == R.id.start_dis)
		{
			if((wifi_flag = true)&&(IPCar != null)&&(cameraIP != null)&& (!cameraIP.equals("")))
			{
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(bitmap != null)
						{
							shape_recognition(bitmaps);
						} else {
							Toast.makeText(CarShapeDemoActivity.this, "无摄像头图片"+"/n"+"请重启摄像头", Toast.LENGTH_LONG).show();
						}
					}
				}).start();
			} else {
				Toast.makeText(CarShapeDemoActivity.this, "未连接网络", Toast.LENGTH_LONG).show();
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
		intent.setClass(CarShapeDemoActivity.this, SearchService.class);
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


	private int rectNum=0;//矩形
	private int triaNum=0;//三角形
	private int circNum=0;//圆形
	// 显示图片
	@SuppressLint("HandlerLeak")
	public Handler phHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 10)
			{
				show_view.setImageBitmap(bitmap);
			}
			if(msg.what == 20)
			{
				wifi_ip.setText(IPCar);
			}
			if(msg.what == 30)
			{
				Camera_ip.setText(cameraIP);
			}
			if(msg.what == 40)
			{
				r_num.setText(""+red_r);
				g_num.setText(""+green_g);
				blue_num.setText(""+blue_b);
			}
			if(msg.what == 50)
			{
				fruit_show.setText("红色圆形");
			}
			if(msg.what == 51)
			{
				fruit_show.setText("绿色圆形");
			}
			if(msg.what == 52)
			{
				fruit_show.setText("蓝色圆形");
			}


			if(msg.what == 60)
			{
				fruit_show.setText("红色三角形");
			}
			if(msg.what == 61)
			{
				fruit_show.setText("绿色三角形");
			}
			if(msg.what == 62)
			{
				fruit_show.setText("蓝色三角形");
			}

			if(msg.what == 70)
			{
				fruit_show.setText("红色矩形");
			}
			if(msg.what == 71)
			{
				fruit_show.setText("绿色矩形");
			}
			if(msg.what == 72)
			{
				fruit_show.setText("蓝色矩形");
			}
			if(msg.what == 80)
			{
				Toast.makeText(CarShapeDemoActivity.this, "请纠正算法", Toast.LENGTH_LONG).show();
			}
		}
	};

	private int red_max=0,green_max=0,blues_max=0;
	private int red_min=0,green_min=0,blues_min=0;

	private void get_input_threshold()
	{
		red_max = Integer.parseInt(r_max.getText().toString());
		green_max = Integer.parseInt(g_max.getText().toString());
		blues_max = Integer.parseInt(blue_max.getText().toString());

		red_min = Integer.parseInt(r_min.getText().toString());
		green_min = Integer.parseInt(g_min.getText().toString());
		blues_min = Integer.parseInt(blue_min.getText().toString());
	}


	// // 储存图片左边像素坐标
	ArrayList<Coordinates> rlistl = new ArrayList<Coordinates>();
	ArrayList<Coordinates> glistl = new ArrayList<Coordinates>();
	ArrayList<Coordinates> blistl = new ArrayList<Coordinates>();
	// // 储存图片右边像素坐标
	ArrayList<Coordinates> rlistr = new ArrayList<Coordinates>();
	ArrayList<Coordinates> glistr = new ArrayList<Coordinates>();
	ArrayList<Coordinates> blistr = new ArrayList<Coordinates>();
	private int RGB_num = 0;

	private Bitmap convertToBlack(Bitmap bip, int index) {// 像素处理背景变为黑色，红绿蓝不变
		RGB_num = index;
		int width = bip.getWidth();
		int height = bip.getHeight();
		int[] pixels = new int[width * height];
		bip.getPixels(pixels, 0, width, 0, 0, width, height);
		int[] pl = new int[bip.getWidth() * bip.getHeight()];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				int pixel = pixels[offset + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if (index==1&& r >red_min && g < green_max && b < blues_max)     // 红色
					pl[offset + x] = 0xFFFF0000;
				else if(index==2&& r <red_max && g > green_min && b < blues_max)  // 绿色
					pl[offset + x] = 0xFF00FF00;
				else if (index==3&&r < red_max && g <green_max && b > blues_min)  // 蓝色
					pl[offset + x] = 0xFF0000FF;
				else
					pl[offset + x] = 0xff000000;// 黑色
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		result.setPixels(pl, 0, width, 0, 0, width, height);
		return result;
	}

	// 沉睡
	public void yanchi(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	ArrayList<Coordinates> list = new ArrayList<Coordinates>();
	ArrayList<Coordinates> list_above = new ArrayList<Coordinates>();
	ArrayList<Coordinates> list_among = new ArrayList<Coordinates>();
	ArrayList<Coordinates> list_below = new ArrayList<Coordinates>();
	private int cnum = 2;
	private boolean flag_go = true;
	private void shape_recognition(Bitmap bitmap_new)
	{
		list.clear();						//清空列表
		list_above.clear();
		list_among.clear();
		list_below.clear();
		int width = bitmap_new.getWidth();		//得到
		int height = bitmap_new.getHeight();

		System.out.println("图片总高度"+height);
		System.out.println("图片总宽度"+width);
		int[] pixels = new int[width * height];
		bitmap_new.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int y = 0; y< height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y*width + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if ((r>200) && (g<100) && (b<100)) { // 红色
					list.add(new Coordinates(x, y));
				}
				if ((r<100) && (g>200) && (b<100)) { // 绿色
					list.add(new Coordinates(x, y));
				}
				if ((r<100) && (g<100) && (b>200)) {// 蓝色
					list.add(new Coordinates(x, y));
				}
			}
		}
		int A_h = 0;
		int B_h = 0;
		int AB_h = 0;
		if((list.size() - cnum )>0)
		{
			A_h = list.get(cnum).getY();
			B_h = list.get(list.size()-cnum).getY();
			if((list.get(list.size()-2).getY() - list.get(2).getY())>0)
			{
				AB_h = list.get(list.size()-2).getY() - list.get(2).getY();
				System.out.println("形状总高度"+AB_h);
				flag_go = true;
			} else {
				flag_go = false;
				Toast.makeText(CarShapeDemoActivity.this, "抠图失败", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(CarShapeDemoActivity.this, "参数设置冲突", Toast.LENGTH_LONG).show();
		}

		int above_A_x = 0;
		int above_B_x = 0;
		int above_AB_x = 0;
		if(flag_go == true)
		{
			for (int x = 0; x < width; x++) {
				int pixel = pixels[(AB_h/3)*width + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if ((r>200) && (g<100) && (b<100)) { // 红色
					list_above.add(new Coordinates(x, A_h));
				}
				if ((r<100) && (g>200) && (b<100)) { // 绿色
					list_above.add(new Coordinates(x, A_h));
				}
				if ((r<100) && (g<100) && (b>200)) {// 蓝色
					list_above.add(new Coordinates(x, A_h));
				}
			}

			if((list_above.size()-cnum) >0)
			{
				above_A_x = list_above.get(cnum).getX();
				above_B_x = list_above.get(list_above.size()-cnum).getX();
				if((above_B_x - above_A_x)>0)
				{
					above_AB_x = list_above.get(list_above.size()-cnum).getX() - list_above.get(cnum).getX();
					System.out.println("上边长度"+above_AB_x);
					flag_go = true;
				} else {
					Toast.makeText(CarShapeDemoActivity.this, "获取基准线一失败", Toast.LENGTH_LONG).show();
					flag_go = false;
				}
			} else {
				Toast.makeText(CarShapeDemoActivity.this, "获取基准线一参数有误", Toast.LENGTH_LONG).show();
				flag_go = false;
			}
		}

		int among_A_x = 0;
		int among_B_x = 0;
		int among_AB_x = 0;
		if( flag_go = true)
		{
			for (int x = 0; x < width; x++) {
				int pixel = pixels[(AB_h/2)*width + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if ((r>200) && (g<100) && (b<100)) { // 红色
					list_among.add(new Coordinates(x, A_h));
				}
				if ((r<100) && (g>200) && (b<100)) { // 绿色
					list_among.add(new Coordinates(x, A_h));
				}
				if ((r<100) && (g<100) && (b>200)) {// 蓝色
					list_among.add(new Coordinates(x, A_h));
				}
			}
			if((list_among.size()-cnum)>0)
			{
				among_A_x = list_among.get(cnum).getX();
				among_B_x = list_among.get(list_among.size()-cnum).getX();
				if((among_B_x - among_A_x)>0)
				{
					among_AB_x = list_among.get(list_among.size()-cnum).getX() - list_among.get(cnum).getX();
					System.out.println("中间长度"+among_AB_x);
					flag_go = true;
				} else {
					Toast.makeText(CarShapeDemoActivity.this, "获取基准线二失败", Toast.LENGTH_LONG).show();
					flag_go = false;
				}
			} else {
				Toast.makeText(CarShapeDemoActivity.this, "获取基准线二参数有误", Toast.LENGTH_LONG).show();
				flag_go = false;
			}
		}

		int below_A_x = 0;
		int below_B_x = 0;
		int below_AB_x = 0;
		if(flag_go == true)
		{

			for (int x = 0; x < width; x++) {
				int pixel = pixels[((AB_h*2)/3)*width + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if ((r>200) && (g<100) && (b<100)) { // 红色
					list_below.add(new Coordinates(x, A_h));
				}
				if ((r<100) && (g>200) && (b<100)) { // 绿色
					list_below.add(new Coordinates(x, A_h));
				}
				if ((r<100) && (g<100) && (b>200)) {// 蓝色
					list_below.add(new Coordinates(x, A_h));
				}
			}
			if((list_below.size()-cnum)>0)
			{
				below_A_x = list_below.get(cnum).getX();
				below_B_x = list_below.get(list_below.size()-cnum).getX();
				if((below_B_x - below_A_x)>0)
				{
					below_AB_x = list_below.get(list_below.size()-cnum).getX() - list_below.get(cnum).getX();
					System.out.println("下边长度"+below_AB_x);
					flag_go = true;
				} else {
					Toast.makeText(CarShapeDemoActivity.this, "获取基准线三失败", Toast.LENGTH_LONG).show();
					flag_go = false;
				}
			} else {
				Toast.makeText(CarShapeDemoActivity.this, "获取基准线三失败", Toast.LENGTH_LONG).show();
				flag_go = false;
			}
		}


		if(((among_AB_x-above_AB_x)<50) && ((among_AB_x - below_AB_x)<50))  //矩形
		{
			switch(RGB_num)
			{
				case 1:
					phHandler.sendEmptyMessage(70);
					break;
				case 2:
					phHandler.sendEmptyMessage(71);
					break;
				case 3:
					phHandler.sendEmptyMessage(72);
					break;
			}
		}
		else
		if((above_AB_x) < (among_AB_x) && (among_AB_x) < (below_AB_x))  //三角形
		{
			switch(RGB_num)
			{
				case 1:
					phHandler.sendEmptyMessage(60);
					break;
				case 2:
					phHandler.sendEmptyMessage(61);
					break;
				case 3:
					phHandler.sendEmptyMessage(62);
					break;
			}
		}
		else
		if((above_AB_x) < (among_AB_x) && (among_AB_x) > (below_AB_x))  //圆形
		{
			switch(RGB_num)
			{
				case 1:
					phHandler.sendEmptyMessage(50);
					break;
				case 2:
					phHandler.sendEmptyMessage(51);
					break;
				case 3:
					phHandler.sendEmptyMessage(52);
					break;
			}
		} else {
			phHandler.sendEmptyMessage(80);
		}
	}

}

