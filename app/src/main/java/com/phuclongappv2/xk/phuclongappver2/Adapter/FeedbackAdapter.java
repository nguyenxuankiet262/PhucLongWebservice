package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phuclongappv2.xk.phuclongappver2.Model.Feedback;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.FeedbackViewHolder;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackViewHolder> {
    Context context;
    List<Feedback> feedbackList;
    AlertDialog alertDialog;
    TextView replied_text, feedback_text;

    public FeedbackAdapter(Context context, List<Feedback> feedbackList){
        this.context = context;
        this.feedbackList = feedbackList;
    }
    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback_layout, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedbackViewHolder holder, final int position) {
        holder.id_feedback.setText(position + 1 + "");
        holder.content_feedback.setText(feedbackList.get(position).getContent());
        if(!TextUtils.isEmpty(feedbackList.get(position).getReply())){
            holder.notreply_feedback.setVisibility(View.INVISIBLE);
            holder.replied_feedback.setVisibility(View.VISIBLE);
        }
        else{
            holder.notreply_feedback.setVisibility(View.VISIBLE);
            holder.replied_feedback.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View item = LayoutInflater.from(context).inflate(R.layout.popup_feedback_layout, null);
                feedback_text = item.findViewById(R.id.feedback_text);
                replied_text = item.findViewById(R.id.replied_text);
                if(!TextUtils.isEmpty(feedbackList.get(position).getReply())){
                    replied_text.setText(feedbackList.get(position).getReply());
                }
                feedback_text.setText(feedbackList.get(position).getContent());
                builder.setView(item);
                alertDialog = builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
}
