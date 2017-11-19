package com.davymoreau.android.beerbook;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

/**
 * Created by davy on 11/10/2017.
 */

public class ColorArrayAdapter extends ArrayAdapter<Color>{

    public ColorArrayAdapter(Context context, ArrayList<Color> colors){
        super(context, simple_list_item_1, colors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Color color = getItem(position);
        return getTextView(color, position);
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        Color color = getItem(i);

        return getTextView(color, i);
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    private TextView getTextView(Color color, int position)
    {
        TextView textView = new TextView(getContext());
        textView.setText(color.getName());

        int dps = 40;
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);

        textView.setHeight(pixels);
        textView.setBackgroundColor(color.getHexCode());

        if (position>4){
            textView.setTextColor(0xffffffff);
        }
        else
        {
            textView.setTextColor(0xff000000);
        }


        return textView;
    }

}
