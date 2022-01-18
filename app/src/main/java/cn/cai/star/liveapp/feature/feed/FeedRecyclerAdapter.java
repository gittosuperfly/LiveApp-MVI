package cn.cai.star.liveapp.feature.feed;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cn.cai.star.liveapp.R;
import cn.cai.star.liveapp.views.LikeView;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {
    private final int[] images = {R.mipmap.im, R.mipmap.im1, R.mipmap.im2, R.mipmap.im3, R.mipmap.im4, R.mipmap.im5, R.mipmap.im6};
    private final String[] videos = {"/raw/v", "/raw/v1", "/raw/v2", "/raw/v3", "/raw/v4", "/raw/v5", "/raw/v6"};

    private final Context mContext;

    public FeedRecyclerAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int index = position;
        while (index >= images.length) {
            index -= images.length;
        }
        holder.videoView.stopPlayback();
        holder.videoView.setVideoURI(Uri.parse("android.resource://" + mContext.getPackageName() + videos[index]));
        Glide.with(mContext).load(images[index]).into(holder.coverView);
        holder.coverView.setAlpha(1f);
        holder.videoView.setAlpha(0f);

    }

    @Override
    public int getItemCount() {
        return 100;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverView;
        VideoView videoView;
        LikeView likeView;

        public ViewHolder(View itemView) {
            super(itemView);

            likeView = itemView.findViewById(R.id.likeView);
            coverView = itemView.findViewById(R.id.cover);
            videoView = itemView.findViewById(R.id.video);
        }
    }
}

