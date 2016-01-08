package thin.blog.knowwell;

/*
* This class conatins all the constant values required for the Application
*/

public class Constants {
    public static final String SHARED_PREFS_USER_DATA = "user_data";
    public static final String USER_DATA_NAME = "name";
    public static final String USER_DATA_EMAIL = "email";
    public static final String USER_DATA_PASSWORD = "password";
    public static final String USER_DATA_USER_ID = "user_id";
    public static final String USER_DATA_IS_GOOGLE_USER = "is_google_user";
    public static final String USER_DATA_GOOGLE_PROFILE_URL = "google_plus_profile_url";
    public static final String USER_DATA_GOOGLE_PROFILE_PHOTO = "google_plus_profile_photo";
    public static final String USER_DATA_GOOGLE_COVER_PHOTO = "google_plus_cover_photo";
    public static final String SUCCESSFUL_LOGIN_HISTORY = "successful_login_history";
    public static final String SUCCESSFUL_REGISTRATION_HISTORY = "successful_registration_history";

    //URL Addresses for Server
    public static final String LOGIN;
    public static final String GOOGLE_LOGIN;
    public static final String SIGNUP;
    public static final String SURVEY_LIST;
    public static final String FETCH_QUESTIONS;
    public static final String SUBMIT_ANSWERS;

    //for testing
    //set localhost = false for testing from webhost
    private static final Boolean localhost = true;

    private static final String ADDRESS;


    static {
        if (localhost) {
            ADDRESS = "http://192.168.1.4:80/knowwell/";
        } else {
            ADDRESS = "http://www.thin.comyr.com/";
        }
        LOGIN = ADDRESS + "login.php";
        GOOGLE_LOGIN = ADDRESS + "googlelogin.php";
        SIGNUP = ADDRESS + "register.php";
        SURVEY_LIST = ADDRESS + "surveylist.php";
        FETCH_QUESTIONS = ADDRESS + "fetchquestions.php";
        SUBMIT_ANSWERS = ADDRESS + "submitanswers.php";
    }
}
