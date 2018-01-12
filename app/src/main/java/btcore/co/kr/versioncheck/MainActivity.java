package btcore.co.kr.versioncheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private String device_version;
    private String stroe_version;
    private BackgroundThread mBackgroundThread;

    AlertDialog.Builder alert_confirm;
    AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackgroundThread = new BackgroundThread();
        mBackgroundThread.start();



    }


    public class BackgroundThread extends Thread{
        @Override
        public void  run(){
            stroe_version = MarketVersionChecker.getMarketVersion(getPackageName());

            try{
                device_version = getPackageManager().getPackageInfo(getPackageName(),0).versionName;
            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }

            deviceVersionCheckHandler.sendMessage(deviceVersionCheckHandler.obtainMessage());
        }
    }
    private final DeviceVersionCheckHandler deviceVersionCheckHandler = new DeviceVersionCheckHandler(this);

    private static class DeviceVersionCheckHandler extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;
        public DeviceVersionCheckHandler(MainActivity mainActivity){
            mainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }
        @Override
        public void handleMessage(Message msg){
            MainActivity activity = mainActivityWeakReference.get();
            if(activity != null){
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg){
        if(stroe_version.compareTo(device_version) > 0){
            alert_confirm = new AlertDialog.Builder(MainActivity.this);
            alert_confirm.setTitle("업데이트");
            alert_confirm.setMessage("새로운 버전이 있습니다. \n보다 나은 사용을 위해 업데이트 해 주세요.").setCancelable(false).setPositiveButton("업데이트바로가기",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("URL"));
                            startActivity(intent);
                        }
                    }).setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'No'
                            return;
                        }
                    });
            alert = alert_confirm.create();
            alert.show();
        }else {

            //이미 최신 버전일경우.
        }
    }


}
