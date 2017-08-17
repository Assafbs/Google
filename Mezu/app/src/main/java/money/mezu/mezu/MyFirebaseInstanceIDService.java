package money.mezu.mezu;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("", "MyFirebaseInstanceIDService::onTokenRefresh: " + refreshedToken);
        SessionManager sm = new SessionManager(StaticContext.mContext);
        if (null != sm.getUserId()) {
            FirebaseBackend.getInstance().updateUserNotificationToken(refreshedToken, sm.getUserId());
        }
    }
}
