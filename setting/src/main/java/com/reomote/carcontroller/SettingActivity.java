package com.reomote.carcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.autofit.widget.EditText;
import com.autofit.widget.ScreenParameter;
import com.reomote.carcontroller.utils.FileUtils;
import com.reomote.carcontroller.utils.Utils;
import com.reomote.setting.R;

import org.json.JSONException;
import org.json.JSONObject;


public class SettingActivity extends Activity {
    private static final String IP = "10.2.0.76";
    private static final String PATH = "rtsp://13728735758:abcd1234@10.2.0.76:554/stream1";
    private static final int DURATION = 3000;
    private String mDir;
    private String mPath;
    private EditText mCamera;
    private EditText mCar;
    private EditText mPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenParameter.isNeedSetFont = false;
        setContentView(R.layout.activity_main);
        mCamera = (EditText) findViewById(R.id.camera_ip);
        mCar = (EditText) findViewById(R.id.car_ip);
        mPort = (EditText) findViewById(R.id.port);

        mDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/carController/";
        mPath = mDir + "config.text";
        FileUtils.mkdirs(mDir);
        read();
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(mCamera, mCar, mPort);
            }
        });
    }

    private void read() {
        new Thread() {
            @Override
            public void run() {
                final String data = FileUtils.read(mPath);
                if (!TextUtils.isEmpty(data)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(data);
                                mCamera.setText(obj.optString("camera"));
                                mCar.setText(obj.optString("car"));
                                mPort.setText(obj.optString("port"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }

            }
        }.start();
    }

    private void save(final EditText camera, final EditText car, final EditText port) {
        new Thread() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    object.put("camera", String.valueOf(camera.getText()));
                    object.put("car", String.valueOf(car.getText()));
                    if (!TextUtils.isEmpty(port.getText())) {
                        object.put("port", Utils.parseInt(String.valueOf(port.getText())));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data = object.toString();
                if (FileUtils.write(mPath, data)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SettingActivity.this, "设置成功", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }

            }
        }.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
