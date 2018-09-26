package cn.deali.minimalpoem.utils.http;

import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.deali.minimalpoem.utils.http.tool.HttpRequestData;
import cn.deali.minimalpoem.utils.http.tool.HttpResponseData;
import cn.deali.minimalpoem.utils.http.tool.StreamTool;

public class HttpRequestUtil {

    private static final String TAG = "HttpRequestUtil";

    //设置http连接的头部信息
    private static void setConnHeader(HttpURLConnection conn, String method, HttpRequestData req_data)
            throws ProtocolException {
        conn.setRequestMethod(method);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("Accept", "*/*");
        //IE使用
//		conn.setRequestProperty("Accept-Language", "zh-CN");
//		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C)");
        //firefox使用
        conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        if (req_data.content_type.equals("") != true) {
            conn.setRequestProperty("Content-Type", req_data.content_type);
        }
        if (req_data.x_requested_with.equals("") != true) {
            conn.setRequestProperty("X-Requested-With", req_data.x_requested_with);
        }
        if (req_data.referer.equals("") != true) {
            conn.setRequestProperty("Referer", req_data.referer);
        }
        if (req_data.cookie.equals("") != true) {
            conn.setRequestProperty("Cookie", req_data.cookie);
            Log.d(TAG, "setConnHeader cookie=" + req_data.cookie);
        }
    }

    private static String getRespCookie(HttpURLConnection conn, HttpRequestData req_data) {
        String cookie = "";
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        if (headerFields != null) {
            List<String> cookies = headerFields.get("Set-Cookie");
            if (cookies != null) {
                for (String cookie_item : cookies) {
                    cookie = cookie + cookie_item + "; ";
                }
            } else {
                cookie = req_data.cookie;
            }
        } else {
            cookie = req_data.cookie;
        }
        Log.d(TAG, "cookie=" + cookie);
        return cookie;
    }

    //get文本数据
    public static HttpResponseData getData(HttpRequestData req_data) {
        HttpResponseData resp_data = new HttpResponseData();
        try {
            URL url = new URL(req_data.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setConnHeader(conn, "GET", req_data);

            conn.connect();
            resp_data.content = StreamTool.getUnzipStream(conn.getInputStream(),
                    conn.getHeaderField("Content-Encoding"), req_data.charset);
            resp_data.cookie = conn.getHeaderField("Set-Cookie");
            resp_data.status_code = conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            resp_data.err_msg = e.getMessage();
            resp_data.success = false;
        }
        return resp_data;
    }

    public static String getHtml(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            byte[] data = StreamTool.readInputStream(in);
            return new String(data, "UTF-8");
        }
        return null;
    }

    //get图片数据
    public static HttpResponseData getImage(HttpRequestData req_data) {
        HttpResponseData resp_data = new HttpResponseData();
        try {
            URL url = new URL(req_data.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setConnHeader(conn, "GET", req_data);
            conn.connect();

            InputStream is = conn.getInputStream();
            resp_data.bitmap = BitmapFactory.decodeStream(is);
            resp_data.cookie = conn.getHeaderField("Set-Cookie");
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            resp_data.err_msg = e.getMessage();
            resp_data.success = false;
        }
        return resp_data;
    }

    //post的内容放在url中
    public static HttpResponseData postUrl(HttpRequestData req_data) {
        HttpResponseData resp_data = new HttpResponseData();
        String s_url = req_data.url;
        if (req_data.params != null) {
            s_url += "?" + req_data.params.toString();
        }
        Log.d(TAG, "s_url=" + s_url);
        try {
            URL url = new URL(s_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setConnHeader(conn, "POST", req_data);
            conn.setDoOutput(true);

            conn.connect();
            resp_data.content = StreamTool.getUnzipStream(conn.getInputStream(),
                    conn.getHeaderField("Content-Encoding"), req_data.charset);
            resp_data.cookie = conn.getHeaderField("Set-Cookie");
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            resp_data.err_msg = e.getMessage();
            resp_data.success = false;
        }
        return resp_data;
    }

    //post的内容放在输出流中
    public static HttpResponseData postData(HttpRequestData req_data) {
        req_data.content_type = "application/x-www-form-urlencoded";
        HttpResponseData resp_data = new HttpResponseData();
        String s_url = req_data.url;
        Log.d(TAG, "s_url=" + s_url + ", params=" + req_data.params.toString());
        try {
            URL url = new URL(s_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setConnHeader(conn, "POST", req_data);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(req_data.params.toString());
            out.flush();

            resp_data.content = StreamTool.getUnzipStream(conn.getInputStream(),
                    conn.getHeaderField("Content-Encoding"), req_data.charset);
            resp_data.cookie = getRespCookie(conn, req_data);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            resp_data.err_msg = e.getMessage();
            resp_data.success = false;
        }
        return resp_data;
    }

    //post的内容分段传输
    public static HttpResponseData postMultiData(HttpRequestData req_data, Map<String, String> map) {
        HttpResponseData resp_data = new HttpResponseData();
        String s_url = req_data.url;
        Log.d(TAG, "s_url=" + s_url);
        String end = "\r\n";
        String hyphens = "--";
        try {
            URL url = new URL(s_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setConnHeader(conn, "POST", req_data);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + req_data.boundary);
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            StringBuffer buffer = new StringBuffer();
            Log.d(TAG, "map.size()=" + map.size());
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String str = it.next();
                buffer.append(hyphens + req_data.boundary + end);
                buffer.append("Content-Disposition: form-data; name=\"");
                buffer.append(str);
                buffer.append("\"" + end + end);
                buffer.append(map.get(str));
                buffer.append(end);
                Log.d(TAG, "key=" + str + ", value=" + map.get(str));
            }
            if (map.size() > 0) {
                buffer.append(hyphens + req_data.boundary + end);
                byte[] param_data = buffer.toString().getBytes(req_data.charset);
                OutputStream out = conn.getOutputStream();
                out.write(param_data);
                out.flush();
            }

            conn.connect();
            resp_data.content = StreamTool.getUnzipStream(conn.getInputStream(),
                    conn.getHeaderField("Content-Encoding"), req_data.charset);
            resp_data.cookie = conn.getHeaderField("Set-Cookie");
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            resp_data.err_msg = e.getMessage();
            resp_data.success = false;
        }
        return resp_data;
    }

}
