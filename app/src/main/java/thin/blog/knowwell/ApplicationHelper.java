package thin.blog.knowwell;

import android.app.Application;
import android.content.Context;

/**
 * Created by jmprathab on 09/12/15.
 */
public class ApplicationHelper extends Application {
    private static ApplicationHelper sInstance;

    public static ApplicationHelper getMyApplicationInstance() {
        return sInstance;
    }

    public static Context getMyApplicationContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
