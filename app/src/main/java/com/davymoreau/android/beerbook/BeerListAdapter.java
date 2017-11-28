package com.davymoreau.android.beerbook;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davymoreau.android.beerbook.database.BeerTastingContract;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by davy on 19/07/2017.
 */

public class BeerListAdapter extends RecyclerView.Adapter<BeerListAdapter.BeerHolder> {

    private ArrayList<ContentValues> mList;
    Context mContext;

    final private BeerClickListener mOnClickListner;

    public BeerListAdapter (BeerClickListener listener){
        mOnClickListner = listener;
    }

    public interface BeerClickListener {
        void onClick(int ClickedItemIndex);
    }

    public void setBeers(ArrayList list) {
        mList = list;
        if(mList != null) notifyDataSetChanged();
    }

    public void addBeer(ContentValues cv){
        mList.add(cv);
        notifyDataSetChanged();
    }

    @Override
    public BeerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int beerItem = R.layout.beer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParent = false;

        View view = inflater.inflate(beerItem, parent, shouldAttachToParent);
        return new BeerHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ContentValues getItem(int position){
        return mList.get(position);
    }


    @Override
    public void onBindViewHolder(final BeerHolder holder, int position) {
        if(position>=mList.size()) return;
        ContentValues contentValues = mList.get(position);
        // name
        String name = contentValues.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_NAME);
        holder.mTvBeerName.setText(name);
        // brasserie
        String brewery = contentValues.getAsString(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY);
        holder.mTvBrewery.setText(brewery);
        // alcohol
        float alcohol = contentValues.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE);
        holder.mTVAlcool.setText(String.valueOf(alcohol) + "°");
        // rating
        float rating = contentValues.getAsFloat(BeerTastingContract.BeerTastingEntry.COLUMN_RATING);
        holder.mRating.setRating(rating);
        //id
        if (contentValues.containsKey(BeerTastingContract.BeerTastingEntry._ID)) {
            long id = (long) contentValues.getAsLong(BeerTastingContract.BeerTastingEntry._ID);
            holder.itemView.setTag(id);
        }
        // pic
        String pic = "";

        if(contentValues.containsKey("picpath")) {
            pic = contentValues.getAsString("picpath");
        }
        File file = new File(pic);

        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Glide.with(mContext)
                    .load(uri)
                    .listener(new RequestListener<Uri, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.mprogress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.mprogress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.mPic);
        } else {
            Drawable defaultPic = mContext.getResources().getDrawable(R.drawable.biere);
            holder.mPic.setImageDrawable(defaultPic);
            holder.mprogress.setVisibility(View.GONE);
        }

    }

    class BeerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTvBeerName;
        TextView mTVAlcool;
        TextView mTvBrewery;
        RatingBar mRating;
        ImageView mPic;
        ProgressBar mprogress;

        public BeerHolder(View itemView)  {
            super(itemView);

            mTvBeerName = (TextView) itemView.findViewById(R.id.tv_beer_name);
            mTVAlcool = (TextView) itemView.findViewById(R.id.tv_alcohol);
            mTvBrewery = (TextView) itemView.findViewById(R.id.tv_brewery);
            mRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mPic = (ImageView) itemView.findViewById(R.id.iv_beer);
            mprogress = (ProgressBar)itemView.findViewById(R.id.progressBarItem);

            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListner.onClick(clickedPosition);
        }
    }
}
