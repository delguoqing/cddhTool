package com.delguoqing.listentext;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.icu.util.Output;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/11.
 */

public class RobService extends AccessibilityService {

    private static String TAG = "RobService";
    private static int PORT = 8988;
    private ServerSocket mServerSocket;
    private Socket mClientSocket;

    private String mQuestion;
    private String[] mAnswer;
    private boolean hasNewQuestion;
    private String[] testStrings = {"hello", "world", "test"};
    private int testIndex = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        setNewQuestionFlag(false);
        // comment this to turn off test
        testIndex = 0;


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "run: create server socket!");
//                try {
//                    mServerSocket = new ServerSocket(PORT);
//                } catch (IOException e) {
//                    Log.d(TAG, "run: create socket failed!");
//                    return;
//                }
//
//                while (true) {
//                    Log.d(TAG, "run: start listening!!!");
//                    if (mClientSocket != null) {
//                        try {
//                            mClientSocket.close();
//                        } catch (IOException e) {
//                            Log.d(TAG, "run: mClientSocket close failed!");
//                        }
//                        mClientSocket = null;
//                    }
//                    try {
//                        mClientSocket = mServerSocket.accept();
//                        OutputStream os = mClientSocket.getOutputStream();
//                        while (true) {
//
//                            if (hasNewQuestion) {
//                                setNewQuestionFlag(false);
//                                byte[] bytes = mQuestion.getBytes();
//                                os.write(bytes.length);
//                                os.write(bytes);
//                                for (int i = 0; i < 3; ++ i) {
//                                    bytes = mAnswer[i].getBytes();
//                                    os.write(bytes.length);
//                                    os.write(bytes);
//                                }
//                            } else {
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                    // do nothing here;
//                                }
//                            }
//                        }
//                    } catch (IOException e) {
//                        Log.d(TAG, "run: accept socket failed!");
//                        continue;
//                    }
//                }
//            }
//        });
    }

    private synchronized void setNewQuestionFlag(boolean b)
    {
        hasNewQuestion = b;
        if (testIndex >= 0) {
            hasNewQuestion = (testIndex < testStrings.length);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo windowNode = event.getSource();
                String question = getQuestion(windowNode);
                String[] options = getOptions(windowNode);
                if (question != "") {
                    Log.d(TAG, "onAccessibilityEvent: ");
                    Log.d(TAG, "\tquestion:" + question);
                    int i = 0;
                    for (String option: options) {
                        Log.d(TAG, "answer" + String.valueOf(i) + ":" + option);
                        ++ i;
                    }
                }
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                windowNode = event.getSource();
                question = getQuestion(windowNode);
                options = getOptions(windowNode);
                if (question != "") {
                    Log.d(TAG, "onAccessibilityEvent: ");
                    Log.d(TAG, "\tquestion:" + question);
                    int i = 0;
                    for (String option: options) {
                        Log.d(TAG, "answer" + String.valueOf(i) + ":" + option);
                        ++ i;
                    }
                }
                break;
        }

        Log.d(TAG, "onAccessibilityEvent: " + event.toString());

    }

    public String getQuestion(AccessibilityNodeInfo root)
    {
        AccessibilityNodeInfo node = findViewByID(root, "com.chongdingdahui.app:id/tvMessage");
        if (node == null) {
            return "";
        }
        CharSequence text = node.getText();
        if (text == null) {
            return "";
        }
        return text.toString();
    }

    public String[] getOptions(AccessibilityNodeInfo root)
    {
        ArrayList<String> options = new ArrayList<String>();
        for (int i = 0; i < 3; ++ i)
        {
            AccessibilityNodeInfo node = findViewByID(root, "com.chongdingdahui.app:id/answer" + String.valueOf(i));
            if (node == null) {
                continue;
            }
            CharSequence text = node.getText();
            if (text == null) {
                continue;
            }
            options.add(text.toString());
        }
        String[] result = new String[options.size()];
        return options.toArray(result);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(AccessibilityNodeInfo root, String id) {
        if (root == null) {
            root = getRootInActiveWindow();
        }
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId(id);

        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    @Override
    public void onInterrupt() {
    }
}
