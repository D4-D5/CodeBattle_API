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
    public static final String CODEFORCES_IS_NULL = "Codeforces ID cannot be empty";
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
    //codeforces profile fetch url
    public static final String CODEFORCES_PROFILE_URI = "https://codeforces.com/api/user.info";
    public static final String CODEFORCES_ID_FETCH_ERROR = "Error occurred while fetching codeforces detail";

    //Create Problem constants
    public static final String PROBLEM_IS_NULL = "Problem cannot be empty";
    public static final String PROBLEM_TITLE_IS_NULL = "Problem title cannot be empty";
    public static final String PROBLEM_STATEMENT_IS_NULL = "Problem statement cannot be empty";
    public static final String PROBLEM_INPUT_SPEC_IS_NULL = "Problem input specification cannot be empty";
    public static final String PROBLEM_OUTPUT_SPEC_IS_NULL = "Problem Output specification cannot be empty";
    public static final String PROBLEM_TEST_INPUT_IS_NULL = "Problem input test cases cannot be empty";
    public static final String PROBLEM_TEST_OUTPUT_IS_NULL = "Problem output test cases cannot be empty";
    public static final String PROBLEM_SAMPLE_INPUT_IS_NULL = "Problem input sample cases cannot be empty";
    public static final String PROBLEM_SAMPLE_OUTPUT_IS_NULL = "Problem output sample cases cannot be empty";
    public static final String PROBLEM_TIME_LIMIT_IS_INVALID = "Problem time limit is invalid";
    public static final String PROBLEM_MEMORY_LIMIT_IS_INVALID = "Problem memory limit is invalid";
    public static final String PROBLEM_AUTHOR_IS_NULL = "Problem authorID cannot be empty";
    public static final String AUTHOR_DO_NOT_HAVE_ACCESS = "Author do not have access rights to create problem";
    public static final String AUTHOR_ID_NOT_FOUND = "No author found using author id";
    public static final String DIFFICULTY_LEVEL_IS_NULL = "Difficulty level is not specified";

    //File Storage Locations
    public static final String FILE_BASE_PATH = "/media/mohit/1AB6DA39B6DA155B/uploads/";
    public static final String FILE_INVALID_PATH = "Sorry! Filename contains invalid path sequence";
    public static final String FILE_ERROR = "Sorry! Error occurred while storing files!";
}

