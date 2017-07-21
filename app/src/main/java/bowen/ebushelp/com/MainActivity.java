package bowen.ebushelp.com;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //显示的相关信息
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mServiceStatusView = (TextView) findViewById(R.id.tv_service_status);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mHandler = new Handler();
        initHandler();
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

    //开打操作页面
    public void onButtonClicked(View view) {
        Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(mAccessibleIntent);
    }

    TextView mServiceStatusView;
    //服务状态更新
    public void updateServiceStatus() {
        boolean serviceEnabled = false;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.EBusHelpService")) {
                serviceEnabled = true;
            }
        }
        mServiceStatusView.setText(serviceEnabled ? "辅助服务已开启" : "辅助服务已关闭");
    }

    //开始预订
    public void startBook(){
        Calendar instance = Calendar.getInstance();
        int hourOfDay = instance.get(Calendar.HOUR_OF_DAY);
    }

    private void jumpToEbus(){
        //String pkg代表包名，String download代表下载url
        final PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("zxzs.ppgj");
        if (null != intent) {//没有获取到intent
            startActivity(intent);
        }
    }

    //去执行任务
    private void initHandler(){
       /* mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar instance = Calendar.getInstance();
                int hourOfDay = instance.get(Calendar.HOUR_OF_DAY);
                if(hourOfDay>=12){
                    jumpToEbus();
                }else{
                    initHandler();
                }
            }
        },500);*/
    }

    //点击进入
    public void onEnterBtnClick(View view){
        jumpToEbus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
    }

}
