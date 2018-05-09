package com.oumardiallo636.gtuc.troskymate.search;

/**
 * Created by oumar on 2/7/18.
 */

public interface SearchActivityMVP {

    interface View {
        void submitQuery(android.view.View view);
        void displayRecentQueries();
    }
    interface Presenter{
        void findPlace();


    }
    interface Model{}
}
