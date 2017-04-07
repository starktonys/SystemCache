package me.stark.tony.systemcache;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import java.lang.reflect.Method;

public class PackageInfo extends Activity {
  private TextView tv;
  private static final String ATTR_PACKAGE_STATS = "PackageStats";

  /** Called when the activity is first created. */
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tv = new TextView(this);
    setContentView(tv);
    getpkginfo("me.stark.tony.systemcache");
  }

  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 1:
          String infoString = "";
          PackageStats newPs = msg.getData().getParcelable(ATTR_PACKAGE_STATS);
          if (newPs != null) {
            infoString += "应用程序大小: " + formatFileSize(newPs.codeSize);
            infoString += "\n数据大小: " + formatFileSize(newPs.dataSize);
            infoString += "\n缓存大小: " + formatFileSize(newPs.cacheSize);
          }
          tv.setText(infoString);
          break;
        default:
          break;
      }
    }
  };

  public void getpkginfo(String pkg) {
    PackageManager pm = getPackageManager();
    try {
      Method getPackageSizeInfo =
          pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
      getPackageSizeInfo.invoke(pm, pkg, new PkgSizeObserver());
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  class PkgSizeObserver extends IPackageStatsObserver.Stub {
    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
      Message msg = mHandler.obtainMessage(1);
      Bundle data = new Bundle();
      data.putParcelable(ATTR_PACKAGE_STATS, pStats);
      msg.setData(data);
      mHandler.sendMessage(msg);
    }
  }

  /**
   * 获取文件大小
   */
  public static String formatFileSize(long length) {
    String result = null;
    int sub_string = 0;
    if (length >= 1073741824) {
      sub_string = String.valueOf((float) length / 1073741824).indexOf(".");
      result = ((float) length / 1073741824 + "000").substring(0, sub_string + 3) + "GB";
    } else if (length >= 1048576) {
      sub_string = String.valueOf((float) length / 1048576).indexOf(".");
      result = ((float) length / 1048576 + "000").substring(0, sub_string + 3) + "MB";
    } else if (length >= 1024) {
      sub_string = String.valueOf((float) length / 1024).indexOf(".");
      result = ((float) length / 1024 + "000").substring(0, sub_string + 3) + "KB";
    } else if (length < 1024) result = Long.toString(length) + "B";
    return result;
  }
}