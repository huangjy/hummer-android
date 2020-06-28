package com.hummer.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.hummer.core.Hummer;
import com.hummer.core.bridge.HMJSContext;
import com.hummer.core.component.network.IRequestCallback;
import com.hummer.core.component.view.HMView;
import com.hummer.core.jni.JSValue;
import com.hummer.demo.loader.HMJavaScriptLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by XiaoFeng on 2019/3/22.
 */
public class DemoActivity extends AppCompatActivity {

    private String jsURL;
    private ViewGroup mainLayout;
    private HMJSContext jsContext;
    private ImageView ivImage;

    public static void launch(Context context, String jsURL) {
        Intent intent = new Intent(context, DemoActivity.class);
        intent.putExtra("JS_URL", jsURL);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("maxiee", "DemoActivity onCreate");

        setContentView(R.layout.activity_demo);

        jsURL = getIntent().getStringExtra("JS_URL");

        initView();
        iniEnviroment();
        loadJSWithURL(jsURL);

//        testNJ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SystemUtil.REQUEST_CAMERA:
                try {
                    Uri uri = SystemUtil.getContentUri(this, SystemUtil.CAMERA_OUTPUT_PATH);
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    showResultImage(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case SystemUtil.REQUEST_ALBUM:
                if (data != null && data.getData() != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        showResultImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SystemUtil.callPhoneDirectly(this, "10086");
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SystemUtil.openCamera(this);
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (ivImage != null) {
            hideResultImage();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        jsContext.onDestroy();
        jsContext = null;
    }

    private void iniEnviroment() {
        jsContext = Hummer.getInstance().createNewContext(mainLayout);
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getActivityTitle(jsURL));

        mainLayout = findViewById(R.id.layout_main);
    }

    private void showResultImage(Bitmap bitmap) {
        ivImage = new ImageView(this);
        ivImage.setImageBitmap(bitmap);
        mainLayout.addView(ivImage);
    }

    private void hideResultImage() {
        mainLayout.removeView(ivImage);
        ivImage = null;
    }

    private String getActivityTitle(String jsURL) {
        return jsURL.substring(jsURL.lastIndexOf("/") + 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.refresh:
                iniEnviroment();
                loadJSWithURL(jsURL);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载js文件内容
     * @param jsURL js文件url
     */
    private void loadJSWithURL(final String jsURL) {
        Log.v("zdf", "loadJSWithURL: " + jsURL);
        if (TextUtils.isEmpty(jsURL)) {
            return;
        }

        HMJavaScriptLoader.loadBundleWithURL(jsURL, new IRequestCallback() {
            @Override
            public void onComplete(final String result) {
                Log.i("zdf", "loadJSWithURL, onComplete: \n" + result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long time1 = System.currentTimeMillis();
                        Log.i("zdf", "## evaluateScript start");
                        JSValue retValue = jsContext.context().evaluateScript(result, jsURL);
                        Log.i("zdf", "## evaluateScript end, time cose: " + (System.currentTimeMillis() - time1));
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void testNJ() {
        String s = "var a = 1;var sum = a + 5;";
        JSValue retValue = jsContext.context().evaluateScript(s, null);
        Log.v("zdf","retValue = " + retValue);
        if (retValue == null) {
            return;
        }
        Log.v("zdf","retValue.toNumber() = " + retValue.toNumber());
//        final NJView rootView = (NJView) retValue.toObject();
//        if (rootView != null) {
//            mainLayout.addView(rootView);
//        }
    }
}
