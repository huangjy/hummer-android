package com.hummer.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

/**
 * Created by XiaoFeng on 2019/3/27.
 */
public class SystemUtil {

    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_ALBUM = 2;

    public static final String CAMERA_OUTPUT_PATH = "/sdcard/temp.jpg";

    /**
     * 拨打电话（直接拨打电话）
     *
     * @param context
     * @param phoneNum 电话号码
     */
    public static void callPhoneDirectly(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param context
     * @param phoneNum 电话号码
     */
    public static void callPhoneDial(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 打开系统相机
     *
     * @param activity
     */
    public static void openCamera(Activity activity) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            Uri uri = SystemUtil.getContentUri(activity, SystemUtil.CAMERA_OUTPUT_PATH);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(activity, "没有系统相机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开系统相册
     *
     * @param activity
     */
    public static void openAlbum(Activity activity) {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(albumIntent, REQUEST_ALBUM);
    }

    public static Uri getContentUri(Context context, String filePath) {
        Uri contentUri;
        File file = new File(filePath);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        return contentUri;
    }
}
