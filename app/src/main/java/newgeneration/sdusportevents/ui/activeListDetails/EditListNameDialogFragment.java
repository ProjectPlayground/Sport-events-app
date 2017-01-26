package newgeneration.sdusportevents.ui.activeListDetails;

import android.app.Dialog;
import android.os.Bundle;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;


import java.util.HashMap;

import newgeneration.sdusportevents.R;
import newgeneration.sdusportevents.model.EventList;
import newgeneration.sdusportevents.utils.Constants;

/**
 *  user edit the list name for all copies of the current list
 */
public class EditListNameDialogFragment extends EditListDialogFragment {
    private static final String LOG_TAG = EventListDetailsActivity.class.getSimpleName();
    String mListName;


    public static EditListNameDialogFragment newInstance(EventList eventList, String listId,
                                                         String encodedEmail) {
        EditListNameDialogFragment editListNameDialogFragment = new EditListNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(eventList,
                R.layout.dialog_edit_list, listId, encodedEmail);
        bundle.putString(Constants.KEY_LIST_NAME, eventList.getListName());
        editListNameDialogFragment.setArguments(bundle);
        return editListNameDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListName = getArguments().getString(Constants.KEY_LIST_NAME);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);

        helpSetDefaultValueEditText(mListName);
        return dialog;
    }


    protected void doListEdit() {
        final String inputListName = mEditTextForList.getText().toString();
        

        if (!inputListName.equals("")) {

            if (mListName != null && mListId != null) {


                if (!inputListName.equals(mListName)) {
                    Firebase shoppingListRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS).
                            child(mListId);


                    HashMap<String, Object> updatedProperties = new HashMap<String, Object>();
                    updatedProperties.put(Constants.FIREBASE_PROPERTY_LIST_NAME, inputListName);



                    shoppingListRef.updateChildren(updatedProperties);
                }
            }
        }

    }
}

