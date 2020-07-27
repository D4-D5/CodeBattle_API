package com.example.CodeWar.app;

public class Constants {
    //status
    public static final String STATUS = "status";
    public static final String STATUS_FAILURE = "failure";
    public static final String STATUS_SUCCESS = "success";
    //reasons
    public static final String REASON = "reason";
    public static final String NUMBER_NOT_REGISTERED = "NUMBER_NOT_REGISTERED";
    public static final String USER_NOT_REGISTERED = "Coder not found";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String INVALID_ARGUMENT = "Invalid Arguments";
    public static final String USER_ID_TAKEN = "CodeWarId Already Taken";
    public static final String PHONE_NUMBER_TAKEN = "Phone Number Already In Use";
    public static final String EMAIL_TAKEN = "EmailId Already In Use";
    public static final String CODEFORCES_ID_TAKEN = "CodeforcesId Already In Use";
    public static final String USERNAME_IS_NULL = "UserName cannot be empty";
    public static final String EMAIL_IS_NULL = "UserName cannot be empty";
    public static final String PASSWORD_IS_NULL = "UserName cannot be empty";
    public static final String COUNTRY_CODE_IS_NULL = "UserName cannot be empty";
    public static final String PHONE_NUMBER_IS_NULL = "UserName cannot be empty";
    public static final String NAME_IS_NULL = "UserName cannot be empty";
    public static final String USER_ROLE_IS_NULL = "UserName cannot be empty";
    public static final String EMAIL_INVALID = "Email ID is not valid";
    public static final String PASSWORD_TOO_WEAK = "Password is too weak!!!! save yourself";
    //message
    public static final String MESSAGE = "message";
    //email regex
    public static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    //password regex
    public static final String PASSWORD_REGEX = "((?=.*\\d).{6,20})";

    //Account verification
    public static final String ACCOUNT_VERIFIED = "Account is verified";
    public static final String ACCOUNT_NOT_VERIFIED = "The link is invalid or broken!";

    //API route to confirm-account
    public static final String URI_CONFIRM_ACCOUNT = "http://localhost:9090/api/confirm-account";

}
