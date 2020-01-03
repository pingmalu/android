package com.malu.hahajing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String CMD_MY_MY =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 06FB";

    private ConsumerIrManager irManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        if (!irManager.hasIrEmitter()) {
            Toast.makeText(this, "未找到红外发生器", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    malu2while bbc = new malu2while(hex2ir(CMD_MY_MY));
                    while (true) {
                        bbc.dosend();
                    }
                }
            });
            thread.start();
        }
    }

    private class malu2while {
        private final IRCommand cmd;

        public malu2while(final IRCommand cmd) {
            this.cmd = cmd;
        }

        public void dosend() {

            irManager.transmit(cmd.freq, cmd.pattern);

            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private IRCommand hex2ir(final String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        frequency = (int) (1000000 / (frequency * 0.241246));
        int pulses = 1000000 / frequency;
        int count;

        int[] pattern = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i), 16);
            pattern[i] = count * pulses;
        }

        return new IRCommand(frequency, pattern);
    }

    private class IRCommand {
        private final int freq;
        private final int[] pattern;

        private IRCommand(int freq, int[] pattern) {
            this.freq = freq;
            this.pattern = pattern;
        }
    }

    public void Click(View view) {
        switch (view.getId()) {
            case R.id.button2://此处是对布局中设置的id直接进行判断，
                // 不需要对控件进行获取（findviewByID）
                Toast.makeText(this, "按钮被点击", Toast.LENGTH_SHORT).show();
                exitApp();
                break;
        }
    }

    public void exitApp() {
//        public void exitApp(Context context){
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
//        for (ActivityManager.AppTask appTask : appTaskList) {
//            appTask.finishAndRemoveTask();
//        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
