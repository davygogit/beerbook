package com.davymoreau.android.beerbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.davymoreau.android.beerbook.database.BeerTastingContract;

import java.util.ArrayList;

/**
 * Created by davy on 19/07/2017.
 */

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.BeerHolder> {

    private Cursor mCursor;
    private ArrayList<ContentValues> mList;
    Context mContext;

    final private BeerClickListener mOnClickListner;

    public BeerAdapter (BeerClickListener listener){
        mOnClickListner = listener;
    }

    public interface BeerClickListener {
        void onClick(int ClickedItemIndex);
    }

    public void setBeers(Cursor cursor) {
            if (mCursor != null) mCursor.close();
            mCursor = cursor;
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
        return mCursor.getCount();
    }

    public Cursor getItem(int position){
        mCursor.moveToPosition(position);
        return mCursor;
    }


    @Override
    public void onBindViewHolder(BeerHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;
        // name
        String name = mCursor.getString(mCursor.getColumnIndex(BeerTastingContract.BeerTastingEntry.COLUMN_NAME));
        holder.mTvBeerName.setText(name);
        // brasserie
        String brewery = mCursor.getString(mCursor.getColumnIndex(BeerTastingContract.BeerTastingEntry.COLUMN_BREWERY));
        holder.mTvBrewery.setText(brewery);
        // alcohol
        String alcohol = mCursor.getString(mCursor.getColumnIndex(BeerTastingContract.BeerTastingEntry.COLUMN_DEGREE));
        holder.mTVAlcool.setText(alcohol + "°");
        // rating
        float rating = mCursor.getFloat(mCursor.getColumnIndex(BeerTastingContract.BeerTastingEntry.COLUMN_RATING));
        holder.mRating.setRating(rating);
        //id
        Long id = mCursor.getLong(mCursor.getColumnIndex(BeerTastingContract.BeerTastingEntry._ID));
        holder.itemView.setTag(id);


    }

    class BeerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTvBeerName;
        TextView mTVAlcool;
        TextView mTvBrewery;
        RatingBar mRating;
        ImageView mPic;

        public BeerHolder(View itemView)  {
            super(itemView);

            mTvBeerName = (TextView) itemView.findViewById(R.id.tv_beer_name);
            mTVAlcool = (TextView) itemView.findViewById(R.id.tv_alcohol);
            mTvBrewery = (TextView) itemView.findViewById(R.id.tv_brewery);
            mRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mPic = (ImageView) itemView.findViewById(R.id.iv_beer);

            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListner.onClick(clickedPosition);
        }
    }
}
