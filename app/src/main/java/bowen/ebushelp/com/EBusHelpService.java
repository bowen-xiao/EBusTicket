package bowen.ebushelp.com;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/26.
 */
public class EBusHelpService extends AccessibilityService {

    String TAG = "EBusHelpService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        Log.w("className :",className + "::className");
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.i("className :",className + "::className");
                if(className.equals("zxzs.ppgj.ui.activity.MainActivity")){
                    //首页，需要切换到线路查询
                    //switchLineSearch();
                }else if(className.equals("zxzs.ppgj.ui.activity.WallActivityNew")){
                    //searchButton("品质公交");
                }else if(className.contains("check.BuyActivity")){
                    bookTick();
                }else if(className.contains("materialdialogs.MaterialDialog")){
                    selectPayType();
                }else if(className.contains("my.SZTCardActivity")){
                    payBtn();
                }
                break;
        }

        int eventType = event.getEventType();
        String eventText = "";
        Log.i(TAG, "==============Start====================");
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "TYPE_VIEW_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventText = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventText = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventText = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventText = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                eventText = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventText = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                eventText = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                eventText = "TYPE_VIEW_SCROLLED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                eventText = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
        }
        eventText = eventText + ":" + eventType;
        Log.i(TAG, eventText);
        Log.i(TAG, "=============END=====================");
    }

    //切换到线路查询页面
    private void switchLineSearch(){
        // 切换页面
        searchButton("");
        // 1 输入线路编号
        // 2 结果页面点击需要的线路
        //
    }

    /**
     * 订票页面，去选择车票信息
     */
    @SuppressLint("NewApi")
    private void bookTick(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        if (nodeInfo != null) {

            List<AccessibilityNodeInfo> nextMonth = recylerView(nodeInfo);
            if(nextMonth != null && nextMonth.size() != 0){
//                AccessibilityNodeInfo imageView = nextMonth.get(0);
                //printNodeInfo(TAG + " :: nextMonth ::",child);
                //月份
                CharSequence text = mSelectMonth.getText();
                Calendar instance = Calendar.getInstance();
                String currentMonth = (instance.get(Calendar.YEAR)) + "年"
                           +(instance.get(Calendar.MONTH) + 2) + "月";
                if(text != null && text.toString().contains(currentMonth)){
                   // mPreMonth.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else{
                    mNextMonth.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                if(mSelectAllDays != null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bookFlow();
                        }
                    },30);
                }
            }
        }
    }

    //预订流程
    @SuppressLint("NewApi")
    private void bookFlow(){
        //如果没有选中，就进行选中
        if(!mSelectAllDays.isChecked()){
            mSelectAllDays.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            //重新刷新控件
            recylerView(getRootInActiveWindow());
        }
        if(mAllDaysMoney != null){
            CharSequence text = mAllDaysMoney.getText();
            if(text != null ){
                Float totalMoney = -1.0f;
                try {
                    totalMoney = Float.valueOf(text.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    totalMoney = -1.0f;
                }
                if(totalMoney > 0){
                    mBookBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else{
                    //bookFlow();
                    //bookTick();
                    // // TODO: 2017/6/26 关闭页面刷新,再次进入刷新
                    backPrePage();
                }
            }else{
                //回到上个月
                mPreMonth.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }



    AccessibilityNodeInfo mSelectAllDays;
    AccessibilityNodeInfo mAllDaysMoney;
    AccessibilityNodeInfo mSelectMonth;
    AccessibilityNodeInfo mNextMonth;
    AccessibilityNodeInfo mPreMonth;
    AccessibilityNodeInfo mBookBtn;
    @SuppressLint("NewApi")
    private List<AccessibilityNodeInfo> recylerView(AccessibilityNodeInfo nodeInfo){
        List<AccessibilityNodeInfo> results = new ArrayList<>();
        if(nodeInfo == null){return  results;}
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            //printNodeInfo(TAG + " :: bookTick ::",child);
            if(child != null && child.getText() != null){
                String text = child.getText().toString();
                if(text.contains("年") && text.contains("月")){
                    mSelectMonth = child;
                    Calendar instance = Calendar.getInstance();
                    String moth = (instance.get(Calendar.MONTH) + 2)+ "月";
                    Log.e(TAG,(text.endsWith(moth)) + " is nextMonth");
                    AccessibilityNodeInfo imageNexMonth = nodeInfo.getChild(i + 1);
                    //下一个月
                    if(imageNexMonth.getClassName().toString().contains("ImageView")
                            && imageNexMonth.isClickable()
                            //&& !(text.endsWith(moth))
                            ){
                        results.add(imageNexMonth);
                        mNextMonth = imageNexMonth;
                    }
                    AccessibilityNodeInfo imagePreMonth = nodeInfo.getChild(i - 1);
                    //上一个月
                    if(imagePreMonth.getClassName().toString().contains("ImageView")
                            && imagePreMonth.isClickable()
                            ){
                        mPreMonth = imagePreMonth;
                    }
                }else if(text.contains("全选")){
                    AccessibilityNodeInfo imageNexMonth = nodeInfo.getChild(i - 1);
                    if(imageNexMonth.getClassName().toString().contains("CheckBox")){
                        mSelectAllDays = imageNexMonth;
                    }
                    AccessibilityNodeInfo selectDays = nodeInfo.getChild(i + 1);
                    if(selectDays.getClassName().toString().contains("TextView")){
                        mAllDaysMoney = selectDays;
                    }
                }else if(text.contains("购买")){
                    if(child.getClassName().toString().contains("Button")
                            && child.isClickable()
                            ){
                        mBookBtn = child;
                    }
                }
            }
            //去子类里面找
            results.addAll(recylerView(child));
        }
        return results;
    }

    @SuppressLint("NewApi")
    private void printNodeInfo(String tag,AccessibilityNodeInfo nodeInfo){
        Log.e(tag,  "=====  begin =====");
        String className = nodeInfo.getClassName().toString();
        Log.e(tag, className.toString() + "");
        Log.e(tag,nodeInfo.getViewIdResourceName() + "");
        Log.e(tag,nodeInfo.getText() + "");
        Log.e(tag,  "=====  end =====");
    }

    /**
     * 查找到
     */
    @SuppressLint("NewApi")
    private void searchButton(String searchKey) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        if (nodeInfo != null) {
            Set<AccessibilityNodeInfo> paketList = new HashSet<>();
            int childCount = nodeInfo.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                String className = child.getClassName().toString();

                Log.i("child ClassName", className.toString() + "");
                Log.i("child ResourceName",child.getViewIdResourceName() + "");
                //			Log.w("child",child.toString() + "");
                if(className.contains("TextView")){
                    paketList.add(child);
                    Log.i("TextView", "text" + child.getText() );
                    if(child.getText().toString().contains(searchKey)
                            && !TextUtils.isEmpty(searchKey)){
                        AccessibilityNodeInfo imageView = nodeInfo.getChild(i - 1);
                        String childClassName = imageView.getClassName().toString();
                        if(childClassName.contains("ImageView") && imageView.isClickable()){
                            imageView.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
/*
            for (AccessibilityNodeInfo n : paketList) {
                //Log.w("searchButton",n.getText().toString() + "");
                if(n.isClickable()){
                    n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }*/
        }
    }


    //选择支付方式
    @SuppressLint("NewApi")
    private  void selectPayType(){
        // 1) 找到需要操作的控件
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        findPayItem(nodeInfo);
        // 2) 点击按钮操作
        if(mPayType != null){
            mPayType.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    AccessibilityNodeInfo mPayType;
    //找到相关的控件
    private void findPayItem(AccessibilityNodeInfo nodeInfo){
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            printNodeInfo(TAG + " :: selectPayType ::",child);
            String className = child.getClassName().toString();
            CharSequence text = child.getText();
            if(className.contains("TextView")
                    && text != null){
                if(text.toString().contains("深圳通")){
                    AccessibilityNodeInfo parent = child.getParent();
                    if(parent.isClickable()){
                        mPayType = parent;
                    }
                }
            }
            if(mPayType == null ){
                if(child.getChildCount() >0 ){
                    findPayItem(child);
                }
            }
        }
    }

    //支付按钮
    AccessibilityNodeInfo mPayBtn;
    //支付按钮
    @SuppressLint("NewApi")
    private void payBtn(){
        // 1) 找到需要操作的控件
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        findPayBtn(nodeInfo);
        // 2) 点击按钮操作
        if(mPayBtn != null){
            Toast.makeText(this,"可以预订",Toast.LENGTH_SHORT).show();
            //mPayBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }



    //找到相关的控件
    private void findPayBtn(AccessibilityNodeInfo nodeInfo){
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
//            printNodeInfo(TAG + " :: selectPayType ::",child);
            String className = child.getClassName().toString();
            CharSequence text = child.getText();
            if(className.contains("Button")
                    && text != null){
                if(text.toString().contains("预订") && child.isClickable()){
                    mPayBtn = child;
                }
            }
            if(mPayBtn == null ){
                if(child.getChildCount() >0 ){
                    findPayItem(child);
                }
            }
        }
    }

    //支付按钮
    AccessibilityNodeInfo mBackBtn;
    //支付按钮
    @SuppressLint("NewApi")
    private void backPrePage(){
        // 1) 找到需要操作的控件
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        findBackBtn(nodeInfo);
        // 2) 点击按钮操作
        if(mBackBtn != null){
            mBackBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }


    //找到相关的控件
    private void findBackBtn(AccessibilityNodeInfo nodeInfo){
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            printNodeInfo(TAG + " :: findBackBtn ::",child);
            String className = child.getClassName().toString();
            CharSequence text = child.getText();
            if(className.contains("ImageView")
                    && text == null){
                if(child.isClickable()){
                    mBackBtn = child;
                }
            }
            if(mBackBtn == null ){
                if(child.getChildCount() >0 ){
                    findBackBtn(child);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
