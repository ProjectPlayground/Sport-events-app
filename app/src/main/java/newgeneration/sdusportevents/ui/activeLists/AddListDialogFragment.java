package newgeneration.sdusportevents.ui.activeLists;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newgeneration.sdusportevents.R;
import newgeneration.sdusportevents.model.EventList;
import newgeneration.sdusportevents.utils.Constants;


public class AddListDialogFragment extends DialogFragment {
    String mEncodedEmail;
    EditText mEditTextListName, et_search;
    Spinner mSpinnerSport;
    Button btnSearch;
    Address mAddress;
    Boolean checkLocation = false;
    public static AddListDialogFragment newInstance(String encodedEmail) {
        AddListDialogFragment addListDialogFragment = new AddListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_ENCODED_EMAIL, encodedEmail);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_list, null);
        mEditTextListName = (EditText) rootView.findViewById(R.id.edit_text_list_name);
        mSpinnerSport = (Spinner)rootView.findViewById(R.id.spinner);
        et_search = (EditText) rootView.findViewById(R.id.et_search);
        btnSearch = (Button)rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String g = et_search.getText().toString();

                Geocoder geocoder = new Geocoder(v.getContext());
                List<Address> addresses = null;

                try {
                    // Getting a maximum of 3 Address that matches the input
                    // text
                    addresses = geocoder.getFromLocationName(g, 3);
                    if (addresses != null && !addresses.equals("")){
                        mAddress = addresses.get(0);
                        Toast.makeText(getActivity(), "Location is founds!",
                                Toast.LENGTH_LONG).show();
                        checkLocation = true;
                    }


                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Location doesn't exist!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        String[] items = new String[]{"football", "basketball"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        mSpinnerSport.setAdapter(adapter);


        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addShoppingList();
                }
                return true;
            }
        });


        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(checkLocation==true){
                            addShoppingList();
                        }
                        else {
                            Toast.makeText(getActivity(), "Can't create event!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return builder.create();
    }


    public void addShoppingList() {
        String userEnteredName = mEditTextListName.getText().toString();


        if (!userEnteredName.equals("")) {


            Firebase listsRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS);
            Firebase newListRef = listsRef.push();

            Firebase zhanref = new Firebase("https://intense-heat-7947.firebaseio.com/locations");
            Map mLocations = new HashMap();
            Map mCoordinate = new HashMap();
            mCoordinate.put("latitude", mAddress.getLatitude());
            mCoordinate.put("longitude", mAddress.getLongitude());
            mLocations.put("location", mCoordinate);
            mLocations.put("sport",mSpinnerSport.getSelectedItem().toString());
            zhanref.push().setValue(mLocations);



            EventList newEventList = new EventList(userEnteredName, mEncodedEmail );



            newListRef.setValue(newEventList);


            AddListDialogFragment.this.getDialog().cancel();
        }

    }
}

