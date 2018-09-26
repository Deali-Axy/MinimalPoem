package cn.deali.minimalpoem.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.deali.minimalpoem.MainApp;
import cn.deali.minimalpoem.utils.http.HttpRequestUtil;
import cn.deali.minimalpoem.utils.http.tool.HttpRequestData;
import cn.deali.minimalpoem.utils.http.tool.HttpResponseData;

// 检查更新任务
public class CheckUpdateTask extends AsyncTask<Void, Integer, HttpResponseData> {
    private static final String TAG = "CheckUpdateTask";
    private Context mContext;
    private boolean toast = false;

    /**
     * @param context Activity对象
     * @param toast   是否显示 toast 提示最新版本
     */
    public CheckUpdateTask(Context context, boolean toast) {
        this.mContext = context;
        this.toast = toast;
    }

    @Override
    protected HttpResponseData doInBackground(Void... voids) {
        HttpRequestData request = new HttpRequestData(MainApp.getInstance().config.getHost() + "/qapp/get");
        JSONObject json = new JSONObject();
        try {
            json.put("id", MainApp.getInstance().config.getAppId());
        } catch (JSONException ex) {
            Log.e(TAG, ex.getMessage());
            this.cancel(true);
        }
        request.params = new StringBuffer(json.toString());
        return HttpRequestUtil.postData(request);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(mContext, "检查更新时发生错误！", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(HttpResponseData data) {
        super.onPostExecute(data);
        if (data.success) {
            try {
                JSONObject json = new JSONObject(data.content);
                if (json.getString("status").equals("ok")) {
                    JSONObject app = json.getJSONObject("app");

                    if (app.getInt("version") > MainApp.getInstance().config.getVersionCode()) {
                        showUpdateAlert(app);
                    } else {
                        if (toast)
                            Toast.makeText(mContext, "你已经使用最新版本啦~", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException ex) {
                Log.e(TAG, ex.getMessage());
                if (toast)
                    Toast.makeText(mContext, "哦豁，完蛋，解析服务器数据失败~", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                if (toast)
                    Toast.makeText(mContext, "糟糕，在检查更新的时候遇到奇奇怪怪的异常了~", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUpdateAlert(JSONObject app) throws JSONException {
        final String download_link = app.getString("download_link");
        final String detail_link = app.getString("detail_link");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("发现新版本咯~");
        builder.setMessage(String.format(
                "版本：%s\n更新说明：\n%s\n",
                app.getString("version_str"), app.getString("update_description")));
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(download_link);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                mContext.startActivity(intent);
            }
        });

        builder.setNeutralButton("了解详情", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(detail_link);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                mContext.startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}