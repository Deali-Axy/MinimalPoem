package cn.deali.minimalpoem.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.deali.minimalpoem.MainApp;
import cn.deali.minimalpoem.R;
import cn.deali.minimalpoem.bean.Poem;
import cn.deali.minimalpoem.cache.LastPoemCache;
import cn.deali.minimalpoem.db.PoemFavoriteEntityDao;
import cn.deali.minimalpoem.entity.PoemFavoriteEntity;
import cn.deali.minimalpoem.menu.MainMenu;
import cn.deali.minimalpoem.task.CheckUpdateTask;
import cn.deali.minimalpoem.utils.http.HttpRequestUtil;
import cn.deali.minimalpoem.utils.http.tool.HttpRequestData;
import cn.deali.minimalpoem.utils.http.tool.HttpResponseData;

public class PoemActivity extends AppCompatActivity {
    private static final String TAG = "PoemActivity";

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_author)
    TextView tvAuthor;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.sv_content)
    ScrollView svContent;
    @BindView(R.id.fab_refresh)
    FloatingActionButton fabRefresh;
    @BindView(R.id.fab_favorite)
    FloatingActionButton fabFavorite;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    private Poem currentPoem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏颜色处理
        StatusBarUtil.setTransparent(PoemActivity.this);
        setContentView(R.layout.activity_poem);

        ButterKnife.bind(this);

        ViewCompat.setNestedScrollingEnabled(svContent, true);
        setSupportActionBar(toolbar);

        // ActionBar 处理
//        ActionBar bar = getSupportActionBar();
//        if (bar != null)
//            bar.setDisplayHomeAsUpEnabled(false);

        setCurrentPoem(new Poem(0,
                getString(R.string.poem_author),
                getString(R.string.poem_title),
                getString(R.string.poem_content).replace('\n', ',')
        ));

        fabRefresh.setOnClickListener(view -> new GetPoemTask().execute());

        Intent intent = getIntent();
        if (intent.getBooleanExtra("local", false)) {
            setCurrentPoem(new Poem(
                    intent.getIntExtra("id", 0),
                    intent.getStringExtra("author"),
                    intent.getStringExtra("title"),
                    intent.getStringExtra("content")));
        } else {
            // 打开自动刷新一首诗
            Poem lastPoem = LastPoemCache.getLastPoem();
            if (lastPoem == null)
                new GetPoemTask().execute();
            else
                setCurrentPoem(lastPoem);

            // 自动检查更新
            new CheckUpdateTask(this, false).execute();
        }

        // 点击收藏按钮
        fabFavorite.setOnClickListener(v -> {
            PoemFavoriteEntityDao poemFavoriteDao = MainApp.getInstance().daoSession.getPoemFavoriteEntityDao();
            PoemFavoriteEntity poemFavoriteEntity = new PoemFavoriteEntity(
                    currentPoem.getId(),
                    currentPoem.getTitle(),
                    currentPoem.getAuthor(),
                    currentPoem.getContentCsv()
            );

            if (poemFavoriteDao.load(poemFavoriteEntity.getId()) == null) {
                poemFavoriteDao.insert(poemFavoriteEntity);
                Snackbar.make(coordinatorLayout, String.format("《%s》已加入收藏夹", tvTitle.getText()), Snackbar.LENGTH_SHORT).show();
            } else {
                poemFavoriteDao.delete(poemFavoriteEntity);
                Snackbar.make(coordinatorLayout, String.format("《%s》已经从收藏夹移除", tvTitle.getText()), Snackbar.LENGTH_SHORT).show();
            }
        });

        // 自动加载顶部图片
        new GetHeaderImageTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return new MainMenu(this).onOptionsItemSelected(item);
    }


    /**
     * 设置当前的古诗
     *
     * @param poem Poem对象
     */
    private void setCurrentPoem(Poem poem) {
        currentPoem = poem;
        tvAuthor.setText(poem.getAuthor());
        tvTitle.setText(poem.getTitle());
        // 根据不同安卓系统版本做的优化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            tvContent.setText(String.join("\n", poem.getContent()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvContent.setText(poem.getContent().stream().collect(Collectors.joining("\n")));
        } else {
            StringBuilder joinContent = new StringBuilder();
            for (String s : poem.getContent()) {
                joinContent.append(s).append("\n");
            }
            // 删除最后一个换行符
            joinContent.deleteCharAt(joinContent.length() - 1);
            tvContent.setText(joinContent);
        }
    }

    // 加载古诗任务
    class GetPoemTask extends AsyncTask<Void, Integer, HttpResponseData> {

        @Override
        protected HttpResponseData doInBackground(Void... voids) {
            HttpRequestData request = new HttpRequestData("http://dc.deali.cn/api/poem/tang");
            return HttpRequestUtil.getData(request);
        }

        @Override
        protected void onPostExecute(HttpResponseData data) {
            super.onPostExecute(data);
            if (data.success) {
                try {
                    JSONObject jsonObject = new JSONObject(data.content);
                    Poem poem = Poem.parseJsonString(jsonObject.getJSONObject("data").toString());
                    poem.setContent(jsonObject.getJSONObject("data").getJSONArray("content"));
                    LastPoemCache.setLastPoem(poem);
                    setCurrentPoem(poem);
                } catch (JSONException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            } else
                Snackbar.make(coordinatorLayout, "网络连接失败！", Snackbar.LENGTH_SHORT).show();
        }
    }


    // 加载顶部图片任务
    class GetHeaderImageTask extends AsyncTask<Void, Integer, HttpResponseData> {
        @Override
        protected HttpResponseData doInBackground(Void... voids) {
            return HttpRequestUtil.getImage(new HttpRequestData("https://picsum.photos/1080/675?image=957&random&blur"));
        }

        @Override
        protected void onPostExecute(HttpResponseData data) {
            super.onPostExecute(data);
            if (data.success) {
                ivHeader.setImageBitmap(data.bitmap);
            }
        }
    }
}
