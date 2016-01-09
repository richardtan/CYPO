package com.tckr.dukcud.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tckr.dukcud.R;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * This is a custom layout for cards for the application.
 * https://github.com/gabrielemariotti/cardslib/
 * Use the above to find out more information to update it e.t.c.
 */
public class CypoCard extends Card {

    protected CharSequence secondaryTitle;
    protected CharSequence mainTitle;

    private int mainTextColour = 0;
    private int secondaryTextColour = 0;

    private static final int DEFAULT_MAIN_TEXT_COLOUR = R.color.cypo_black;
    private static final int DEFAULT_SECONDARY_TEXT_COLOUR = R.color.cypo_black;

    /**
     * Default Constructor
     * @param context -
     */
    public CypoCard(Context context, String secondaryTitle) {
        this(context, R.layout.cypo_native_inner_base_main, secondaryTitle);
        this.setBackgroundColorResourceId(R.color.cypo_white);
        this.setCardElevation(10);
    }

    /**
     * This will set the card to the CYPO layout
     * @param context -
     * @param innerLayout -
     */
    public CypoCard(Context context, int innerLayout, String secondaryTitle) {
        super(context, innerLayout);
        this.secondaryTitle = secondaryTitle;
    }

    /**
     * This is overriding the existing setupInnerViewElements. Please look at the code at:
     * https://github.com/gabrielemariotti/cardslib/blob/master/library-core/src/main/java/it/gmariotti/cardslib/library/internal/Card.java
     * @param parent -
     * @param view -
     */
    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            // Existing implementation for adding to TextView
            TextView mTitleView = (TextView) view.findViewById(R.id.cypo_card_main_inner_simple_title);
            if (mTitleView != null) {

                // If mainTitle exist, use it, else use the default mTitle for parent class
                if (mainTitle != null) {
                    mTitleView.setText(mainTitle);
                } else {
                    mTitleView.setText(mTitle);
                }

                // Set new colour if available
                if (mainTextColour != 0) {
                    mTitleView.setTextColor(getContext().getResources().getColor(mainTextColour));
                } else {
                    mTitleView.setTextColor(getContext().getResources().getColor(DEFAULT_MAIN_TEXT_COLOUR));
                }

            }

            // Secondary text for the card
            TextView mSecondaryView = (TextView) view.findViewById(R.id.cypo_card_main_inner_simple_title_2);
            if(mSecondaryView != null) {
                mSecondaryView.setText(secondaryTitle);

                if (secondaryTextColour != 0) {
                    mSecondaryView.setTextColor(getContext().getResources().getColor(secondaryTextColour));
                } else {
                    mSecondaryView.setTextColor(getContext().getResources().getColor(DEFAULT_SECONDARY_TEXT_COLOUR));
                }

            }
        }
    }

    /**
     * Use this instead of setTitle from Card/BaseCard to use HTML syntax
     * @param mainTitle - the text message
     */
    public void setMainTitle(CharSequence mainTitle) {
        this.mainTitle = mainTitle;
    }

    /**
     * This method will be used to set the colour to the text
     * @param mainTextColour - Use the resource colour and pass that through
     */
    public void setMainTextColour(int mainTextColour) {
        this.mainTextColour = mainTextColour;
    }

    /**
     * This method will be used to set the colour to the text
     * @param secondaryTextColour - Use the resource colour and pass that through
     */
    public void setSecondaryTitle(int secondaryTextColour) {
        this.secondaryTextColour = secondaryTextColour;
    }
}
