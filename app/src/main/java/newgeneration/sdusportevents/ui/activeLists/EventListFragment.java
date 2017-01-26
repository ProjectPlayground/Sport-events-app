package newgeneration.sdusportevents.ui.activeLists;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;

import newgeneration.sdusportevents.R;
import newgeneration.sdusportevents.model.EventList;
import newgeneration.sdusportevents.ui.activeListDetails.EventListDetailsActivity;
import newgeneration.sdusportevents.utils.Constants;


/**
 * A simple {@link Fragment} subclass that shows a list of all shopping lists a user can see.
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventListFragment extends Fragment {
    private String mEncodedEmail;
    private EventListAdapter mEventListAdapter;
    private ListView mListView;

    public  EventListFragment() {

    }


    public static EventListFragment newInstance(String encodedEmail) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_ENCODED_EMAIL, encodedEmail);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_events_lists, container, false);
        initializeScreen(rootView);


        Firebase activeListsRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS);



        mEventListAdapter = new EventListAdapter(getActivity(), EventList.class,
                R.layout.single_event_list, activeListsRef, mEncodedEmail);



        mListView.setAdapter(mEventListAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventList selectedList = mEventListAdapter.getItem(position);
                if (selectedList != null) {
                    Intent intent = new Intent(getActivity(), EventListDetailsActivity.class);

                    String listId = mEventListAdapter.getRef(position).getKey();
                    intent.putExtra(Constants.KEY_LIST_ID, listId);

                    startActivity(intent);
                }
            }
        });


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventListAdapter.cleanup();
    }


    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_lists);
    }
}
