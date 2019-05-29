package com.example.blago.themoviedb.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.blago.themoviedb.MovieModel.Result;
import com.example.blago.themoviedb.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Result> mList;


    public SearchAdapter(Context context, ArrayList<Result> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.search_item, viewGroup, false);

        return new SearchAdapter.MyViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder myViewHolder, int i) {

        double avrage_count = mList.get(i).getVote_average();
        avrage_count = avrage_count/2;

        myViewHolder.ratingBar.setRating((float) avrage_count);
        LayerDrawable stars = (LayerDrawable) myViewHolder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FA9900"), PorterDuff.Mode.SRC_ATOP);
        Picasso.get().load("https://image.tmdb.org/t/p/w500" + mList.get(myViewHolder.getAdapterPosition()).getPoster_path())
                .into(myViewHolder.img_movie);
        myViewHolder.txt_title.setText(mList.get(myViewHolder.getAdapterPosition()).getTitle());
        myViewHolder.txt_raiting.setText("" + mList.get(i).getVote_average());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title, txt_raiting;
        ImageView img_movie;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_title = (TextView) itemView.findViewById(R.id.txt_name);
            img_movie = (ImageView) itemView.findViewById(R.id.img_movie);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            txt_raiting = (TextView) itemView.findViewById(R.id.txt_raiting);
        }
    }
}
