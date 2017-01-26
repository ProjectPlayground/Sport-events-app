package newgeneration.sdusportevents.ui.activeListDetails;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;

import newgeneration.sdusportevents.R;
import newgeneration.sdusportevents.model.EventList;
import newgeneration.sdusportevents.model.User;
import newgeneration.sdusportevents.ui.BaseActivity;
import newgeneration.sdusportevents.utils.Constants;
import newgeneration.sdusportevents.utils.Utils;


public class EventListDetailsActivity extends BaseActivity {
    private static final String LOG_TAG = EventListDetailsActivity.class.getSimpleName();
    private Firebase mActiveListRef, mCurrentUserRef;

    private Button mButtonTurning;
    private TextView tvv;
    private TextView tv1;
    private TextView tv4;
    private ListView mListView;
    private String mListId;
    private User mCurrentUser;

    private boolean Turning = false;

    private boolean mCurrentUserIsOwner = false;
    private EventList mEventList;
    private ValueEventListener mCurrentUserRefListener, mActiveListRefListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_details);


        Intent intent = this.getIntent();
        mListId = intent.getStringExtra(Constants.KEY_LIST_ID);
        if (mListId == null) {

            finish();
            return;
        }


        mActiveListRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS).child(mListId);
        mCurrentUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        Firebase listItemsRef = new Firebase(Constants.FIREBASE_URL_EVENT_LIST_ITEMS).child(mListId);



        initializeScreen();



        mCurrentUserRefListener = mCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) mCurrentUser = currentUser;
                else finish();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,
                        getString(R.string.log_error_the_read_failed) +
                                firebaseError.getMessage());
            }
        });

        final Activity thisActivity = this;



        mActiveListRefListener = mActiveListRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {


                EventList eventList = snapshot.getValue(EventList.class);

                if (eventList == null) {
                    finish();

                    return;
                }

                //ZAKOMENTIT SNIZY
                mEventList = eventList;




                mCurrentUserIsOwner = Utils.checkIfOwner(eventList, mEncodedEmail);



                invalidateOptionsMenu();


                setTitle(eventList.getListName());

                HashMap<String, User> mTurn = mEventList.getUsersTurn();
                if (mTurn != null && mTurn.size() != 0 &&
                        mTurn.containsKey(mEncodedEmail)) {
                    Turning = true;
                    mButtonTurning.setText(getString(R.string.button_stop_turned));
                    mButtonTurning.setBackgroundColor(ContextCompat.getColor(EventListDetailsActivity.this, R.color.dark_grey));
                } else {
                    mButtonTurning.setText(getString(R.string.button_start_turned));
                    mButtonTurning.setBackgroundColor(ContextCompat.getColor(EventListDetailsActivity.this, R.color.primary_dark));
                    Turning = false;

                }

                setWhosShoppingText(mEventList.getUsersTurn());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,
                        getString(R.string.log_error_the_read_failed) +
                                firebaseError.getMessage());
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_list_details, menu);


        MenuItem remove = menu.findItem(R.id.action_remove_list);
        MenuItem edit = menu.findItem(R.id.action_edit_list_name);



        remove.setVisible(mCurrentUserIsOwner);
        edit.setVisible(mCurrentUserIsOwner);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_edit_list_name) {
            showEditListNameDialog();
            return true;
        }


        if (id == R.id.action_remove_list) {
            removeList();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mActiveListRef.removeEventListener(mActiveListRefListener);
        mCurrentUserRef.removeEventListener(mCurrentUserRefListener);
    }


    private void initializeScreen() {

        tvv = (TextView) findViewById(R.id.textView);
        tv1 = (TextView)findViewById(R.id.textView2);
        tv4 =(TextView)findViewById(R.id.textView4);
        mButtonTurning = (Button) findViewById(R.id.button_turning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }


    private void setWhosShoppingText(HashMap<String, User> usersShopping) {

        if (usersShopping != null) {
            ArrayList<String> usersWhoAreNotYou = new ArrayList<>();

            for (User user : usersShopping.values()) {
                if (user != null && !(user.getEmail().equals(mEncodedEmail))) {
                    usersWhoAreNotYou.add(user.getName());
                }
            }

            int numberOfUsersShopping = usersShopping.size();
            String t,t1;
            String t2;


            if (Turning) {
                switch (numberOfUsersShopping) {
                    case 1:
                        t = getString(R.string.text_you_are_turned);
                        t1 = getString(R.string.empty);
                        t2 = getString(R.string.empty);
                        break;
                    case 2:
                        t = String.format(
                                getString(R.string.text_you_are_turned));
                        t1 = String.format(
                                getString(R.string.text_you_and_other_are_turned),
                                usersWhoAreNotYou.get(0));
                        t2 = String.format(
                                getString(R.string.empty));
                        break;
                    case 3:
                        t = String.format(
                                getString(R.string.text_you_are_turned));
                        t1 = String.format(
                                getString(R.string.text_you_and_other_are_turned),
                                usersWhoAreNotYou.get(0));
                        t2 = String.format(
                                getString(R.string.text_you_and_other_are_turned),
                                usersWhoAreNotYou.get(1));
                        break;

                    default:
                        t = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.size());
                        t1 = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.get(0));
                        t2 = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.get(1));
                }

            } else {
                switch (numberOfUsersShopping) {
                    case 1:
                        t = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.get(0));//tut
                        t1 = String.format(
                                getString(R.string.text_you_and_other_are_turned),
                                usersWhoAreNotYou.get(0));
                        t2 = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.get(0));
                        break;
                    case 2:

                        t = String.format(
                                getString(R.string.empty));
                        t1 = String.format(
                                getString(R.string.text_you_and_other_are_turned),
                                usersWhoAreNotYou.get(0));
                        t2 = String.format(
                                getString(R.string.text_you_and_other_are_turned),
                                usersWhoAreNotYou.get(1));
                        break;
                    default:
                        t = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.get(0),
                                usersWhoAreNotYou.size() - 1);
                        t1 = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.get(0),
                                usersWhoAreNotYou.get(1));
                        t2 = String.format(
                                getString(R.string.empty),
                                usersWhoAreNotYou.get(1));

                }
            }
            tvv.setText(t);
            tv1.setText(t1);
            tv4.setText(t2);

        } else {
            tvv.setText("");
        }
    }









    public void addMeal(View view) {
    }


    public void removeList() {

        DialogFragment dialog = RemoveListDialogFragment.newInstance(mEventList, mListId);
        dialog.show(getFragmentManager(), "RemoveListDialogFragment");
    }




    public void showEditListNameDialog() {

        DialogFragment dialog = EditListNameDialogFragment.newInstance(mEventList, mListId, mEncodedEmail);
        dialog.show(this.getFragmentManager(), "EditListNameDialogFragment");

    }





    public void toggleShopping(View view) {

        Firebase usersTurningRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS)
                .child(mListId).child(Constants.FIREBASE_PROPERTY_USERS_TURNING)
                .child(mEncodedEmail);


        if (Turning) {
            usersTurningRef.removeValue();
        } else {
            usersTurningRef.setValue(mCurrentUser);
    }
    }

}
