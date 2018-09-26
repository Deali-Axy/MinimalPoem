package cn.deali.minimalpoem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.deali.minimalpoem.R;
import cn.deali.minimalpoem.activity.PoemActivity;
import cn.deali.minimalpoem.entity.PoemFavoriteEntity;

// RecyclerView 的适配器
// todo 优化日期显示
public class PoemAdapter extends RecyclerView.Adapter<PoemAdapter.ContentHolder> {
    private static final String TAG = "PoemAdapter";

    private ArrayList<PoemFavoriteEntity> poems;

    private Context mContext;
    private RecyclerView mContentRv;

    // item 点击事件监听
    private OnItemClickListener mOnItemClickListener = null;


    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public PoemAdapter(ArrayList<PoemFavoriteEntity> poems, Context context, RecyclerView contentRv) {
        this.poems = poems;
        this.mContext = context;
        this.mContentRv = contentRv;
    }

    @Override
    public PoemAdapter.ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_list_1, parent, false);
        ContentHolder holder = new ContentHolder(view);
        view.setOnClickListener(holder);
        Log.v(TAG, "创建ViewHolder 绑定点击事件. viewType=" + viewType);
        return holder;
    }


    // 绑定数据
    @Override
    public void onBindViewHolder(final PoemAdapter.ContentHolder holder, final int position) {
        // 确保不会超出数组边界
        if (position < poems.size()) {
            PoemFavoriteEntity poem = poems.get(position);
            holder.tv_title.setText(poem.getTitle());
            holder.tv_author.setText(poem.getAuthor());


            String content = poem.getContent().replaceAll(",", "，");
            holder.tv_content.setText(content);


            //将position保存在itemView的Tag中，以便点击时进行获取
            Log.v(TAG, String.format("将position=%d保存在itemView的Tag中，以便点击时进行获取", position));
            Log.v(TAG, "position=" + position + " itemId=" + holder.getItemId());
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return poems.size();
    }


    // ViewHolder
    public class ContentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title;
        public TextView tv_content;
        public TextView tv_author;

        public ContentHolder(View itemView) {
            super(itemView);
            Log.v(TAG, "itemView ID=" + itemView.getId());
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(itemView, getAdapterPosition());
                Log.v(TAG, "ContentHolder里面响应点击事件。getAdapterPosition=" + getAdapterPosition());
            }
        }
    }


}