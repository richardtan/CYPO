package com.tckr.dukcud.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapterFork;
import it.gmariotti.cardslib.library.extra.staggeredgrid.view.CardGridStaggeredViewFork;

/**
 * This class is used as a first go to for the staggered grid view provided by the cardlib
 * library. Because of a bug in a library that the cardlib uses (from etsy.android.grid) we have
 * to fork that class and therefore fork the cardlib class.
 *
 *  This class below is used as an interfacing class so if any changes is made, then we can just
 *  make the change to this one class and therefore would not need to refactor anything else.
 *
 *  FORKED ->
 *      https://github.com/venkat230278/AndroidStaggeredGridViewOverscrollBug/blob/master/patch/sg.patch
 *      The above URL contains the patch I am using for the staggered grid view
 */
public class CypoGridStaggeredView extends CardGridStaggeredViewFork {

    public CardGridStaggeredArrayAdapterFork mCardArrayAdapter;

    public CypoGridStaggeredView(Context context) {
        super(context);

    }

    public CypoGridStaggeredView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CypoGridStaggeredView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void debugging() {
        /*System.out.println("TOP: " + getHighestChildTop());
        System.out.println("BOTTOM: " + getLowestChildBottom());

        if(getLowestChildBottom() > 2000000) {
            mCardArrayAdapter.notifyDataSetChanged();
        }*/

    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        debugging();
        return super.onInterceptTouchEvent(ev);

    }
}

