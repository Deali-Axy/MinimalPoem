package cn.deali.minimalpoem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.deali.minimalpoem.MainApp;
import cn.deali.minimalpoem.R;
import cn.deali.minimalpoem.adapter.PoemAdapter;
import cn.deali.minimalpoem.db.PoemFavoriteEntityDao;
import cn.deali.minimalpoem.entity.PoemFavoriteEntity;
import cn.deali.minimalpoem.listener.RecyclerItemClickListener;

public class FavoriteActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;

    private ArrayList<PoemFavoriteEntity> poem_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlRefresh.setRefreshing(false);
            }
        });

        // 卡片视图设置
        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        rvContent.setItemAnimator(new DefaultItemAnimator());        // 设置 RecyclerView 默认动画效果
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);                          // 设置视图逆序显示。
        layoutManager.setReverseLayout(true);
        rvContent.setLayoutManager(layoutManager);
        rvContent.addOnItemTouchListener(new RecyclerItemClickListener(rvContent, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < poem_list.size()) {
                    PoemFavoriteEntity poem = poem_list.get(position);
                    Intent intent = new Intent(FavoriteActivity.this, PoemActivity.class);
                    intent.putExtra("local", true);         // 是否本地页面的标志
                    intent.putExtra("position", position);
                    intent.putExtra("id", poem.getId());
                    intent.putExtra("title", poem.getTitle());
                    intent.putExtra("author", poem.getAuthor());
                    intent.putExtra("content", poem.getContent());
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        // 从数据库里加载收藏列表
        PoemFavoriteEntityDao poemDao = MainApp.getInstance().daoSession.getPoemFavoriteEntityDao();
        poem_list = new ArrayList<>();
        poem_list.addAll(poemDao.loadAll());
        rvContent.setAdapter(new PoemAdapter(poem_list, this, rvContent));
    }
}
