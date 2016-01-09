package com.tckr.dukcud;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tckr.dukcud.data.DataSharedPreferencesDAO;
import com.tckr.dukcud.view.CardGeneration;
import com.tckr.dukcud.view.CypoGridStaggeredView;

import java.util.ArrayList;
import java.util.Collections;

import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapterFork;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Fragment to go along with the Main Activity
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = "MainFragment";

    CypoGridStaggeredView cardGridView;
    boolean intentYesterday = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // See if we have came from the notification. If so then set the intentYesterday to true.
        try {
            intentYesterday = getActivity().getIntent().getExtras().getBoolean("yesterday");
        } catch (NullPointerException e) {
            Log.e(TAG, "onCreate - Nothing on the Bundle: " + e);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Add the cards to the view
        cardGridView = (CypoGridStaggeredView) view.findViewById(R.id.cardGridView);

        // Set the arrayAdapter
        ArrayList<Card> cards = new ArrayList<>();
        cardGridView.mCardArrayAdapter = new CardGridStaggeredArrayAdapterFork(getActivity(), cards);
        cardGridView.setAdapter(cardGridView.mCardArrayAdapter);

        // Load cards
        new LoaderCardsAsyncTask().execute();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Method to create cards for the UI.
     * @return array list of cards
     */
    private ArrayList<Card> initCards() {

        // An array of card to add to the UI
        ArrayList<Card> cards = new ArrayList<>();
        CardGeneration cardGeneration = new CardGeneration(this.getActivity());

        DataSharedPreferencesDAO dsp = new DataSharedPreferencesDAO(this.getActivity());

        // Add today's card
        cards.add(cardGeneration.getTodayCard());

        // Only add yesterday's card if it exist
        Card yesterdayCard = cardGeneration.getYesterdayCard();
        if (yesterdayCard != null) {
            cards.add(yesterdayCard);
        }

        // If we came from the notification, then we want to display yesterday first.
        if(intentYesterday && yesterdayCard != null){
            Collections.swap(cards, 0, 1);
        }

        // Add all time card
        cards.add(cardGeneration.getAllTimeCard());

        // Add the screen on time card
        Card allTimeChargeSOTCard = cardGeneration.getAllTimeChargeSOTCard();
        if (allTimeChargeSOTCard != null) {
            cards.add(allTimeChargeSOTCard);
        }

        // Add the all time insight card
        Card alltimeInsight = cardGeneration.getAllTimeInsightCard();
        if (alltimeInsight != null) {
            cards.add(alltimeInsight);
        }

        // Add the month insight card
        Card monthInsight = cardGeneration.getMonthInsightCard();
        if (monthInsight != null) {
            cards.add(monthInsight);
        }

        // Add the week insight card
        Card weekInsight = cardGeneration.getWeekInsightCard();
        if (weekInsight != null) {
            cards.add(weekInsight);
        }

        // See if we have a new wear device
        boolean newWearDevice = dsp.getDataBoolean(DataSharedPreferencesDAO.KEY_NEW_WEAR_DEVICE);
        if (!intentYesterday && newWearDevice) {
            cards.add(0, cardGeneration.newWearDeviceCard());
        }

        // Add Welcome card on first start up
        boolean welcomeComplete = dsp.getDataBoolean(DataSharedPreferencesDAO.KEY_WELCOME_COMPLETE);
        if (!welcomeComplete) {
            cards.add(0, cardGeneration.welcomeCard());
        }

        // Close the database connection
        cardGeneration.closeDatabaseConnection();

        return cards;
    }

    /**
     * Used to get the data for the cards in another method, so it does not impact performance
     * on the main UI thread.
     */
    class LoaderCardsAsyncTask extends AsyncTask<Void, Void, ArrayList<Card>> {

        @Override
        public ArrayList<Card> doInBackground(Void... params) {
            return initCards();
        }

        public void onPostExecute(ArrayList<Card> cards) {
            updateAdapter(cards);
        }

    }

    /**
     * Update the adapter so we can now populate the cards on the UI
     */
    private void updateAdapter(ArrayList<Card> cards) {
        if (cards != null) {
            cardGridView.mCardArrayAdapter.addAll(cards);
            cardGridView.mCardArrayAdapter.notifyDataSetChanged();
        }
    }
}
