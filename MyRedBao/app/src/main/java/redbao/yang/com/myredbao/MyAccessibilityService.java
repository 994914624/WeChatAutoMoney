package redbao.yang.com.myredbao;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;

/**
 * Created by Yang Zhankun on 17/5/28.
 * 无障碍服务
 * 测试微信版本：6.5.8
 * 测试机：红米4X。
 */
public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";
    // 红包打开后的页面
    private final String LUCKY_MONEY_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    // 红包页面
    private final String LUCKY_MONEY_RECEIVER = "com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f";
    /**
     * 存储已打开的红包的标记
     */
    private String lastStr = null;
    private Long lastTime = 0L;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.e(TAG, "事件" + eventType);
        switch (eventType) {
            //每次在聊天界面中有新消息到来时都出触发该事件 4096
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.e(TAG, "666_VIEW_SCROLLED_666");
                //获取当前聊天页面的根布局
                AccessibilityNodeInfo root = getRootInActiveWindow();
                //获取聊天信息
                getWeChatLog(root);
                //findRedPacket(root);
                break;
            // 页面跳转监听 32
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                Log.i(TAG, "WINDOW_STATE_CHANGED-类名： " + className);
                if (LUCKY_MONEY_RECEIVER.equals(className)) {
                    Log.e(TAG, "LUCKY_MONEY_RECEIVER");
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    openRedPacket(rootNode);
                }
                //打开详情页之后，退出红包详情页面
                if (LUCKY_MONEY_DETAIL.equals(className)) {
                    Log.e(TAG, "back-返回");
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
                break;
        }
    }

    /**
     * 开抢红包
     */
    private void openRedPacket(AccessibilityNodeInfo rootNode) {
        Log.e(TAG, "开始抢");
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);

            if ("android.widget.Button".equals(node.getClassName())) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.e(TAG, "拿到了红包-888");
            }
            openRedPacket(node);
        }
    }

    /**
     * 获取聊天的控件
     *
     * @param rootNode
     */
    private void getWeChatLog(AccessibilityNodeInfo rootNode) {
        try {
            if (rootNode != null) {
                //获取所有聊天的线性布局
                List<AccessibilityNodeInfo> listChatRecord = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/p");
                if (listChatRecord.size() == 0) {
                    return;
                }
                //获取最后一行聊天的线性布局（即是最新的那条消息）
                AccessibilityNodeInfo finalNode = listChatRecord.get(listChatRecord.size() - 1);
                if (finalNode != null) {
                    //获取聊天内容
                    GetChatRecord(finalNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从具体的聊天信息中抓取红包信息
     *
     * @param node 最新一条聊天记录的节点
     */
    public void GetChatRecord(AccessibilityNodeInfo node) {
        try {
            if (node != null) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    AccessibilityNodeInfo nodeChild = node.getChild(i);
                    if (nodeChild != null) {
                        Log.e("####", nodeChild.getText() + "");
                        if (null != nodeChild.getText()) {
                            if (nodeChild.getText().toString().equals("领取红包")) {
                                Log.e("####", "8-8-8:" + node);
                                String nowStr = node.toString().substring(98, 140);
                                Long nowTime = System.currentTimeMillis();
                                Log.e("####", nowStr + "-" + (nowTime - lastTime));
                                //通过比较红包在屏幕的位置来避免多次打开红包 boundsInScreen: Rect(104, 914 - 544, 1082）
                                //仅通过判红包在屏幕的位置一个红包仍会打开两次，所以对同一个红包，3秒内会认为是同一红包。
                                if (!nowStr.equals(lastStr) && nowTime - lastTime > 3000) {
                                    Log.e("####", "点击红包");
                                    node.performAction(ACTION_CLICK);
                                    lastStr = nowStr;
                                    lastTime = nowTime;
                                    return;
                                }
                                Log.e("####", "此红包已领过");
                                break;
                            }
                        }
                    }
                    //递归遍历此控件下的所有节点
                    GetChatRecord(nodeChild);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "开始抢红包！！！", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "即将终止", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "抢红包服务已被关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }
}
