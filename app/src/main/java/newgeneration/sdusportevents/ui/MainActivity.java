package newgeneration.sdusportevents.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import newgeneration.sdusportevents.R;
import newgeneration.sdusportevents.model.User;
import newgeneration.sdusportevents.ui.activeLists.AddListDialogFragment;
import newgeneration.sdusportevents.ui.activeLists.EventListFragment;
import newgeneration.sdusportevents.ui.map.AddMapDialogFragment;
import newgeneration.sdusportevents.ui.map.MapFragment;
import newgeneration.sdusportevents.utils.Constants;



public class MainActivity extends BaseActivity {
    private Firebase mUserRef;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ValueEventListener mUserRefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);


        initializeScreen();


        mUserRefListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


                if (user != null) {

                    String firstName = user.getName().split("\\s+")[0];
                    String title = firstName + "'s events";
                    setTitle(title);
                }
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

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserRef.removeEventListener(mUserRefListener);
    }


    public void initializeScreen() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }


    public void showAddListDialog(View view) {

        DialogFragment dialog = AddListDialogFragment.newInstance(mEncodedEmail);
        dialog.show(MainActivity.this.getFragmentManager(), "AddListDialogFragment");
    }


    public void showAddMealDialog(View view) {

        DialogFragment dialog = AddMapDialogFragment.newInstance();
        dialog.show(MainActivity.this.getFragmentManager(), "AddMapDialogFragment");
    }


    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;


            switch (position) {
                case 0:
                    fragment = EventListFragment.newInstance(mEncodedEmail);
                    break;
                case 1:
                    fragment = MapFragment.newInstance();
                    break;
                default:
                    fragment = EventListFragment.newInstance(mEncodedEmail);
                    break;
            }

            return fragment;
        }


        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.pager_title_event_lists);
                case 1:
                default:
                    return getString(R.string.pager_title_map);
            }
        }
    }
}
