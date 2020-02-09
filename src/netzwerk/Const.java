package netzwerk;
/**
 * @author zozzy on 28.01.19
 */
 final class Const {

    static final String TRUE_LOGIN = "trueLogin";
    static final String FALSE_lOGIN = "falseLogin";
    static final String TRUE_REGISTER = "trueRegister";
    static final String FALSE_REGISTER = "falseRegister";
    static final String REG_FAILED = "Registration Failed!";
    static final String USER_KICKED = "kicked";
    static final String USER_LOGGED_IN = "userlogged";
    static final String ONLINE_USERS_REQUEST = "online";
    static final String PLAY_C4 = "play";
    static final String USER_BUSY = "userBusy";
    static final String ANSWER_PLAY_YES = "true";
    static final String ANSWER_PLAY_NO = "false";
    static final String START_GAME = "connect4";
    static final String REQUEST_PLAY_REJECTED = "rejected";
    static final String REQUEST_FAILURE = "Failure";
    static final String REGISTER_REQUEST = "/register";
    static final String LOGIN_REQUEST = "/login";

    static final int DISCONNECT_TOKEN = 55;

    private Const() {
        throw new AssertionError();
    }
}
