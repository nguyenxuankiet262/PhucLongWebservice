package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.phuclongappv2.xk.phuclongappver2.Interface.ILoadMore;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.Model.News;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.ActivityWebView;
import com.squareup.picasso.Picasso;

import java.util.List;

class LoadingViewHolder extends RecyclerView.ViewHolder
{

    public ProgressBar progressBar;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
    }
}

class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public RoundedImageView image_news;
    public TextView date_news, name_news;
    ItemClickListener itemClickListener;

    public NewsViewHolder(View itemView) {
        super(itemView);
        image_news = itemView.findViewById(R.id.image_noti);
        date_news = itemView.findViewById(R.id.time_noti);
        name_news = itemView.findViewById(R.id.name_noti);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;
    ILoadMore loadMore;
    boolean isLoading;
    Context context;
    List<News> newsList;
    int visibleThreshold = 1;
    int lastVisibleItem,totalItemCount;

    public NewsAdapter(RecyclerView recyclerView,Context context, List<News> newsList){
        this.context = context;
        this.newsList = newsList;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalItemCount <= (lastVisibleItem+visibleThreshold))
                {
                    if(loadMore != null)
                        loadMore.onLoadMore();
                    isLoading = true;
                }

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return newsList.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_notification_layout, parent, false);
            return new NewsViewHolder(itemView);
        }
        else if(viewType == VIEW_TYPE_LOADING)
        {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_loading_layout,parent,false);
            return new LoadingViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  NewsViewHolder)
        {
            NewsViewHolder viewHolder = (NewsViewHolder) holder;
            Picasso.with(context).load(newsList.get(position).getImage()).into(viewHolder.image_news);
            viewHolder.date_news.setText(newsList.get(position).getDate());
            viewHolder.name_news.setText(newsList.get(position).getName());
            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    Intent intent = new Intent(context, ActivityWebView.class);
                    intent.putExtra("URL", newsList.get(position).getLink());
                    context.startActivity(intent);
                }
            });
        }
        else if(holder instanceof LoadingViewHolder)
        {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
    public void setLoaded() {
        isLoading = false;
    }
}
