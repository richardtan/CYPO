package com.tckr.dukcud.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tckr.dukcud.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Class for the card header for the UI
 */
public class CypoCardHeader extends CardHeader {

    private int textColourResourceId = 0;
    private static final int DEFAULT_HEADER_TEXT_COLOUR = R.color.cypo_accent;

    public CypoCardHeader(Context context) {
        super(context);
    }

    /**
     * This method sets values to header elements and customizes view.
     * This has been overriden to allow me to change the text style of the header
     *
     * @param parent  parent view (Inner Frame)
     * @param view   Inner View
     */
    @Override
    public void setupInnerViewElements(ViewGroup parent, View view){

        //Add simple title to header
        if (view!=null){

            TextView mTitleView=(TextView) view.findViewById(R.id.card_header_inner_simple_title);

            if (mTitleView!=null) {
                mTitleView.setText(mTitle);

                // Set a new colour if we have something
                if (textColourResourceId != 0) {
                    mTitleView.setTextColor(getContext().getResources().getColor(textColourResourceId));
                } else {
                    mTitleView.setTextColor(getContext().getResources().getColor(DEFAULT_HEADER_TEXT_COLOUR));
                }
            }
        }

    }

    /**
     * This method will be used to set the colour to the text
     * @param textColourResourceId - Use the resource colour and pass that through
     */
    public void setTextColourResourceId(int textColourResourceId) {
        this.textColourResourceId = textColourResourceId;
    }
}
