package thin.blog.knowwell;

/**
 * Created by jmprathab on 09/12/15.
 */
public class Constants {
    public static final String SHARED_PREFS_USER_DATA = "user_data";
    public static final String USER_DATA_NAME = "name";
    public static final String USER_DATA_EMAIL = "email";
    public static final String USER_DATA_PASSWORD = "password";
    public static final String USER_DATA_USER_ID = "user_id";
    public static final String SUCCESSFUL_LOGIN_HISTORY = "successful_login_history";
    public static final String SUCCESSFUL_REGISTRATION_HISTORY = "successful_registration_history";

    //url addresses
    public static final String LOGIN;
    public static final String SIGNUP;
    public static final String SELECT_SURVEY;
    public static final String FETCH_QUESTIONS;
    public static final String SUBMIT_ANSWERS;

    //for testing
    private static final Boolean localhost = false;

    private static final String ADDRESS;

    static {
        if (localhost) {
            ADDRESS = "http://192.168.1.2:80/knowwell/";
        } else {
            ADDRESS = "http://www.thin.comyr.com/";
        }
        LOGIN = ADDRESS + "login.php";
        SIGNUP = ADDRESS + "register.php";
        SELECT_SURVEY = ADDRESS + "choosesurvey.php";
        FETCH_QUESTIONS = ADDRESS + "fetchquestions.php";
        SUBMIT_ANSWERS = ADDRESS + "submitanswers.php";
    }

}
