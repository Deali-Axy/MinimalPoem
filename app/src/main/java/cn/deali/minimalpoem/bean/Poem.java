package cn.deali.minimalpoem.bean;

import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Poem {
    private static final String TAG = "Poem";

    private long id;
    private String title = "";
    private String author = "";
    private ArrayList<String> content = new ArrayList<>();

    public Poem() {
    }

    public Poem(long id, String author, String title, JSONArray contentArray) {
        this.id = id;
        this.setAuthor(author);
        this.setTitle(title);
        this.setContent(contentArray);
    }

    public Poem(String author, String title, JSONArray contentArray) {
        this.setAuthor(author);
        this.setTitle(title);
        this.setContent(contentArray);
    }

    public Poem(long id, String author, String title, String csvContent) {
        this.id = id;
        this.setAuthor(author);
        this.setTitle(title);
        this.setContent(csvContent);
    }

    public Poem(String author, String title, String csvContent) {
        this.setAuthor(author);
        this.setTitle(title);
        this.setContent(csvContent);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    // 返回以逗号分割的内容
    public String getContentCsv() {
        // 根据不同安卓系统版本做的优化
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            return String.join(",", this.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.content.stream().collect(Collectors.joining(","));
        } else {
            StringBuilder joinContent = new StringBuilder();
            for (String s : this.content) {
                joinContent.append(s).append(",");
            }
            // 删除最后一个换行符
            joinContent.deleteCharAt(joinContent.length() - 1);
            return joinContent.toString();
        }
    }

    public void setContent(JSONArray contentArray) {
        for (int i = 0; i < contentArray.length(); i++) {
            try {
                this.content.add(contentArray.getString(i));
            } catch (JSONException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    public void setContent(String csvContent) {
        String[] contentList = csvContent.split(",");
        this.content.addAll(Arrays.asList(contentList));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
