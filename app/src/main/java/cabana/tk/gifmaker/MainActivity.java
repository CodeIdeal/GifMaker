package cabana.tk.gifmaker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import cabana.tk.gifmaker.Base.BaseActivity;
import cabana.tk.gifmaker.Base.Global;
import cabana.tk.gifmaker.Base.MyContext;
import cabana.tk.gifmaker.Service.FloatMenuService;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUESTP_FLOAT_CODE = 100;
    private static final int REQUEST_CAPTURE_CODE = 200;
    private MediaProjectionManager mpm;
    private MediaProjection mMediaProjection;
    private ImageView image;
    private FloatMenuService mFloatMenuService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个FloatMenuService对象
            mFloatMenuService = ((FloatMenuService.MyBinder)service).getService();

            //注册回调接口来接收设置ImageView的动作
            mFloatMenuService.setDisplayImage(new FloatMenuService.DisplayImageListener() {
                @Override
                public void displayImage(Bitmap bitmap) {
                    if(bitmap == null)
                        return;
                    image.setImageBitmap(bitmap);
                }
            });

            mFloatMenuService.setClearImage(new FloatMenuService.ClearImageListener() {
                @Override
                public void ClearImage() {
                    image.setImageResource(R.drawable.ic_menu_camera);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initpermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private void initpermission() {
        //当系统版本大于等于6.0时，悬浮权限要特殊获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                requestAlertWindowPermission();
            } else {
                Toast.makeText(MainActivity.this, "已获取悬浮窗权限可以随时开始工作", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "已获取悬浮窗权限可以随时开始工作", Toast.LENGTH_SHORT).show();
        }

    }

    //发送请求开启悬浮窗
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUESTP_FLOAT_CODE);
    }

    private void initView() {
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //初始化MediaProjectionManager
                    mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                    Intent captureIntent = mpm.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_CAPTURE_CODE);
                }

            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // 侧滑菜单
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        image =    (ImageView) findViewById(R.id.iv_image);
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("ScreenSharingDemo",
                MyContext.ScreenWidth, MyContext.ScreenHeight, MyContext.ScreenDPI,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                Global.getSurface(), null /*Callbacks*/, null /*Handler*/);
    }

    //接受请求结果，并执行请求成功后大的逻辑
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(MainActivity.this, "requestCode" + requestCode + "resultCode" + resultCode, Toast.LENGTH_SHORT).show();
        if (requestCode == REQUESTP_FLOAT_CODE && resultCode == RESULT_OK ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(MainActivity.this, "已获取悬浮窗权限可以随时开始工作", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == REQUEST_CAPTURE_CODE && resultCode == RESULT_OK) {
            mMediaProjection = mpm.getMediaProjection(resultCode, data);
//            mMediaProjection.registerCallback(new MediaProjectionCallback(), null);
            Global.setVirtualDisplay(createVirtualDisplay());
            Toast.makeText(MainActivity.this, "已构造虚拟屏幕映射，时刻准备截屏或录屏", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MyContext.mContext, FloatMenuService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                // Handle the camera action
                break;
            case R.id.nav_gallery:
                //Handle the gallery action
                break;
            case R.id.nav_slideshow:
                //Handle the slideshow action
                break;
            case R.id.nav_manage:
                //Handle the manage action
                break;
            case R.id.nav_share:
                //Handle the share action
                break;
            case R.id.nav_send:
                //Handle the send action
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
