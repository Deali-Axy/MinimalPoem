package cn.deali.minimalpoem.utils.http.tool;

import android.graphics.Bitmap;

public class HttpResponseData {
    public String content;
    public Bitmap bitmap;
    public String cookie;
    public String err_msg;
    public int status_code;
    public boolean success;

    public HttpResponseData() {
        content = "";
        bitmap = null;
        cookie = "";
        err_msg = "";
        status_code = 0;
        success = true;
    }
}

