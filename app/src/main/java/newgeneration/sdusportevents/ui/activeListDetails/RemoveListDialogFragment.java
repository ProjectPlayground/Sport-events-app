package newgeneration.sdusportevents.ui.activeListDetails;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

import newgeneration.sdusportevents.R;
import newgeneration.sdusportevents.model.EventList;
import newgeneration.sdusportevents.utils.Constants;

public class RemoveListDialogFragment extends DialogFragment {
    String mListId;
    final static String LOG_TAG = RemoveListDialogFragment.class.getSimpleName();


    public static RemoveListDialogFragment newInstance(EventList eventList, String listId) {
        RemoveListDialogFragment removeListDialogFragment = new RemoveListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LIST_ID, listId);
        removeListDialogFragment.setArguments(bundle);
        return removeListDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListId = getArguments().getString(Constants.KEY_LIST_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog)
                .setTitle(getActivity().getResources().getString(R.string.action_remove_list))
                .setMessage(getString(R.string.dialog_message_are_you_sure_remove_list))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeList();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        return builder.create();
    }

    private void removeList() {

        HashMap<String, Object> removeListData = new HashMap<String, Object>();

        removeListData.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS + "/"
                + mListId, null);
        removeListData.put("/" + Constants.FIREBASE_LOCATION_EVENT_LIST_ITEMS + "/"
                + mListId, null);

        Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);


        firebaseRef.updateChildren(removeListData, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                if (firebaseError != null) {
                    Log.e(LOG_TAG, getString(R.string.log_error_updating_data) + firebaseError.getMessage());
                }
            }
        });
    }

}
