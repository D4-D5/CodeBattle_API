package com.example.CodeWar.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    //status
    public static final String STATUS = "status";
    public static final String STATUS_FAILURE = "failure";
    public static final String STATUS_SUCCESS = "success";
    //reasons
    public static final String PAYLOAD_IS_NULL = "Payload cannot be empty!";
    public static final String REASON = "reason";
    public static final String NUMBER_NOT_REGISTERED = "NUMBER_NOT_REGISTERED";
    public static final String USER_NOT_REGISTERED = "Coder not found";
    public static final String BAD_CREDENTIALS = "Either codeBattleId or password is incorrect";
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
    public static final String PROBLEM_TIME_LIMIT_IS_INVALID = "Problem time limit is invalid, must be >=1";
    public static final String PROBLEM_MEMORY_LIMIT_IS_INVALID = "Problem memory limit is invalid";
    public static final String PROBLEM_AUTHOR_IS_NULL = "Problem authorID cannot be empty";
    public static final String AUTHOR_DO_NOT_HAVE_ACCESS = "Author do not have access rights to create problem";
    public static final String AUTHOR_ID_NOT_FOUND = "No author found using author id";
    public static final String DIFFICULTY_LEVEL_IS_NULL = "Difficulty level is not specified";
    public static final String PROBLEM_NOT_FOUND = "No problem found with this problem ID";
    public static final String PROBLEM_ID_IS_NULL = "Please specify the problem Id to be updated";
    public static final String PROBLEM_NOT_IN_DRAFT = "Problem is not in draft, so cannot modify";
    public static final String PROBLEM_ALREADY_PUBLISHED = "Problem with this ID is already published";
    public static final String PROBLEM_IS_IN_DRAFT = "Problem is still in draft state, so cannot be published";
    public static final String PROBLEM_ALREADY_IN_REVIEW = "Problem with this ID is already in review";
    public static final String PROBLEM_CONSTRAINTS_IS_NULL = "Problem constraints can't be null";
    public static final String ERROR_TAG = "Error occurred while saving tags";
    public static final String PROBLEM_IDEAL_SOLUTION_IS_NULL = "Problem solution can't be null";

    //File Storage Locations
    public static final String FILE_BASE_PATH = "/media/mohit/1AB6DA39B6DA155B/uploads/";
    public static final String FILE_INVALID_PATH = "Sorry! Filename contains invalid path sequence";
    public static final String FILE_ERROR = "Sorry! Error occurred while handling files!";
    public static final String FILE_MKDIR = "Sorry! Error occurred while creating destination folder";

    //Lobby
    public static final String ROOM_ID_NULL = "Room Id cannot be empty!";
    public static final String OWNER_IS_NULL = "Owner cannot be empty!";
    public static final String CONTEST_TYPE_IS_NULL = "contest type cannot be empty!";
    public static final String ROOM_ID_IN_USE = "Given room id is already in use";
    public static final String ROOM_ID_NOT_FOUND = "Room Id not found";
    public static final String CODEBATTLE_ID_NOT_FOUND = "User with the codeBattle ID not found";
    public static final String NOT_OWNER = "Sorry! Only owner can start the contest";
    public static final String CONTEST_IS_ALREADY_LIVE = "Contest is already live";
    public static final String CONTEST_ENDED = "Contest has ended! ab kch nhi ho  skta";
    public static final String END_CONTEST = "CONTEST_ENDED";
    public static final String CONTEST_STARTED = "CONTEST_STARTED";
    public static final String MINIMUM_CONTESTANT = "Minimum 2 contestant must be there to start the contest!";
    public static final String USER_ALREADY_IN_ROOM = "User already added in contest";


    //Contest Question set
    public static final List<String> CONTEST_QUESTION_SET = Arrays.asList("200","300","210","120","111","021","012","003");
    public static final int INITIAL_RATING = 800;
    public static final double CODEFORCES_TO_CODEBATTLE_FACTOR = 1.5;
    public static final String CONTESTANT_NOT_ALLOWED = "user with codeBattleId is not contestant of this contest";
    public static final String CONTEST_NOT_LIVE = "Contest with roomID is not live";
    public static final String SOURCECODE_IS_NULL = "Source code is empty!";
    public static final String CONTESTANT_NOT_LIVE = "Contestant is not live for the contest";

    //submission
    public static final int SLEEP_TIME = 1000;
    public static final long PENALTY_TIME = 300000;
    public static final long CONTEST_RUN_TIME = 60000000;
    public static final String CODE_SIZE_EXCEEDED = "Maximum code can't be more than 65535 characters";
    public static final long MAX_CODE_SIZE = 65535;
    public static final String WRONG_SOLUTION = "Your solution have some issue, either it didnt match output for given input or something else went wrong";
//    public static final Map<String, Integer> languages = Map.of("cpp",54,"c",50,"java",62,"py",71);
}

