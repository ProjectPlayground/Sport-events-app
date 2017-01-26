package newgeneration.sdusportevents;

import com.firebase.client.Firebase;

/**
 * Includes one-time initialization of Firebase related code
 */
public class SportEventsApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
    }

}