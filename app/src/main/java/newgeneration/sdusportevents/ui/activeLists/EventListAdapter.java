package newgeneration.sdusportevents.ui.activeLists;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import newgeneration.sdusportevents.R;
import newgeneration.sdusportevents.model.EventList;
import newgeneration.sdusportevents.model.User;
import newgeneration.sdusportevents.utils.Constants;



public class EventListAdapter extends FirebaseListAdapter<EventList> {
    private String mEncodedEmail;


     public EventListAdapter(Activity activity, Class<EventList> modelClass, int modelLayout,
                            Query ref, String encodedEmail) {
        super(activity, modelClass, modelLayout, ref);
        this.mEncodedEmail = encodedEmail;
        this.mActivity = activity;
    }


    @Override
    protected void populateView(View view, EventList list) {


        TextView textViewListName = (TextView) view.findViewById(R.id.text_view_list_name);
        final TextView textViewCreatedByUser = (TextView) view.findViewById(R.id.text_view_created_by_user);
        final TextView textViewUsersShopping = (TextView) view.findViewById(R.id.text_view_people_shopping_count);

        String ownerEmail = list.getOwner();


        textViewListName.setText(list.getListName());


        if (list.getUsersTurn() != null) {
            int usersShopping = list.getUsersTurn().size();
            if (usersShopping == 1) {
                textViewUsersShopping.setText(String.format(
                        mActivity.getResources().getString(R.string.person_turned),
                        usersShopping));
            } else {
                textViewUsersShopping.setText(String.format(
                        mActivity.getResources().getString(R.string.person_turned),
                        usersShopping));
            }
        } else {

            textViewUsersShopping.setText("");
        }


        if (ownerEmail != null) {
            if (ownerEmail.equals(mEncodedEmail)) {
                textViewCreatedByUser.setText(mActivity.getResources().getString(R.string.text_you));
            } else {
                Firebase userRef = new Firebase(Constants.FIREBASE_URL_USERS).child(ownerEmail);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            textViewCreatedByUser.setText(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.e(mActivity.getClass().getSimpleName(),
                                mActivity.getString(R.string.log_error_the_read_failed) +
                                        firebaseError.getMessage());
                    }
                });
            }
        }

    }
}
