package com.example.CodeWar.services.implementation;

import com.example.CodeWar.app.UserVerificationStatus;
import com.example.CodeWar.dto.CodeforcesProfile;
import com.example.CodeWar.dto.JwtAuthenticationResponse;
import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.dto.UserPayload;
import com.example.CodeWar.model.ConfirmationToken;
import com.example.CodeWar.model.Problem;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.ConfirmationTokenRepository;
import com.example.CodeWar.repositories.ProblemRepository;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.security.JwtTokenProvider;
import com.example.CodeWar.services.LoginService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.CodeWar.app.Constants.*;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    private EmailSenderServiceImpl emailSenderService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, Object> login(LoginPayload loginPayload) {
        Map<String, Object> response = new HashMap<>();

        //If the payload is NULL
        if (Objects.isNull(loginPayload)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginPayload.getCodeBattleId(),
                            loginPayload.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            response.put(STATUS,STATUS_SUCCESS);
            response.put(MESSAGE,new JwtAuthenticationResponse(jwt));
            return response;
        }
        catch (BadCredentialsException e){
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,BAD_CREDENTIALS);
            return response;
        }
    }


    @Override
    public Map<String, Object> register(UserPayload userPayload) {
        logger.info("mai yaha aa agaya ahu");
        Map<String, Object> response = new HashMap<>();
//        User user = new User();
//        verifyCodeforcesID(user,"mohitagr0001",response);

        //Validate all fields for userPayload
        if (!isValidUserRegister(userPayload, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        User user = new User(userPayload);

        //verify CodeforcesID and fetch rating
        if(userPayload.getCodeforcesId()!=null && !StringUtils.isEmpty(userPayload.getCodeforcesId()) && !userPayload.getCodeforcesId().isBlank()){
            if (!verifyCodeforcesID(user, user.getCodeforcesId(), response)) {
                response.put(STATUS, STATUS_FAILURE);
                return response;
            }
        }
        else{
            user.setRating(INITIAL_RATING);
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Problem> problemSet = new HashSet<>();
        problemSet.add(problemRepository.getOne((long) 97));
        problemSet.add(problemRepository.getOne((long) 112));
        problemSet.add(problemRepository.getOne((long) 118));
        problemSet.add(problemRepository.getOne((long) 122));
        user.getSolvedProblems().addAll(problemSet);

        //Insert the user payload to database
        userRepository.save(user);

        logger.info("This is final user that saved {}",user);

        //send verification mail
        if (!sendEmailVerification(user, user.getEmail(), response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        logger.info("{}",user);

        //response for success
//        response.put(MESSAGE, user);
        response.put(STATUS, STATUS_SUCCESS);
        return response;
    }

    @Override
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userRepository.findAll();
        response.put(STATUS, STATUS_SUCCESS);
        response.put(MESSAGE, users);
        return response;
    }

    @Override
    public Map<String, Object> confirmUserAccount(String confirmationToken) {
        Map<String, Object> response = new HashMap<>();
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
            userRepository.save(user);
            response.put(STATUS, STATUS_SUCCESS);
            response.put(MESSAGE, ACCOUNT_VERIFIED);
        } else {
            response.put(STATUS, STATUS_FAILURE);
            response.put(MESSAGE, ACCOUNT_NOT_VERIFIED);
        }
        return response;
    }

    private boolean isValidUserRegister(UserPayload userPayload, Map<String, Object> response) {
        List<String> reasons = new ArrayList<>();

        //If the user payload is null
        if (Objects.isNull(userPayload)) {
            response.put(REASON, INVALID_ARGUMENT);
            return false;
        }

        //If codeBattleId is null or empty or blank
        //else If the codeBattleId already exists
        if (Objects.isNull(userPayload.getCodeBattleId()) || userPayload.getCodeBattleId().isEmpty() || userPayload.getCodeBattleId().isBlank()) {
            reasons.add(USERNAME_IS_NULL);
        } else if (userRepository.existsByCodeBattleId(userPayload.getCodeBattleId())) {
            reasons.add(USER_ID_TAKEN);
        }

        //If email is null or empty or blank
        //else If email is invalid
        //else If the email already exist
        if (Objects.isNull(userPayload.getEmail()) || userPayload.getEmail().isEmpty() || userPayload.getEmail().isBlank()) {
            reasons.add(EMAIL_IS_NULL);
        } else if (regexMatcher(userPayload.getEmail(), EMAIL_REGEX)) {
            reasons.add(EMAIL_INVALID);
        } else if (userRepository.existsByEmail(userPayload.getEmail())) {
            reasons.add(EMAIL_TAKEN);
        }

        //If password is null or empty or blank
        //else If the password do not match regex
        if (Objects.isNull(userPayload.getPassword()) || userPayload.getPassword().isEmpty() || userPayload.getPassword().isBlank()) {
            reasons.add(PHONE_NUMBER_IS_NULL);
        } else if (regexMatcher(userPayload.getPassword(), PASSWORD_REGEX)) {
            reasons.add(PASSWORD_TOO_WEAK);
        }

        //If countrycode is null or empty or blank
        if (Objects.isNull(userPayload.getCountryCode()) || userPayload.getCountryCode().isEmpty() || userPayload.getCountryCode().isBlank()) {
            reasons.add(COUNTRY_CODE_IS_NULL);
        }

        //If phoneNumber is null or empty or blank
        //else If the phoneNumber already exists
        if (Objects.isNull(userPayload.getPhoneNumber()) || userPayload.getPhoneNumber().isEmpty() || userPayload.getPhoneNumber().isBlank()) {
            reasons.add(PHONE_NUMBER_IS_NULL);
        } else if (userRepository.existsByPhoneNumber(userPayload.getPhoneNumber())) {
            reasons.add(PHONE_NUMBER_TAKEN);
        }

        //If name is null or empty or blank
        if (Objects.isNull(userPayload.getName()) || userPayload.getName().isEmpty() || userPayload.getName().isBlank()) {
            reasons.add(NAME_IS_NULL);
        }

//        If codeforcesID is not null or not empty or not blank
//        then check If the codeforcesID already exists
        if (!Objects.isNull(userPayload.getCodeforcesId()) && !userPayload.getCodeforcesId().isEmpty() && !userPayload.getCodeforcesId().isBlank()) {
            if (userRepository.existsByCodeforcesId(userPayload.getCodeforcesId())) {
                reasons.add(CODEFORCES_ID_TAKEN);
            }
        }


        if (reasons.isEmpty()) {
            return true;
        }
        response.put(REASON, reasons);
        return false;

    }

    private boolean verifyCodeforcesID(User user, String codeforcesId, Map<String, Object> response) {
        String codeforcesURL = CODEFORCES_PROFILE_URI + "?handles=" + codeforcesId;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        LoginServiceImpl.logger.info(codeforcesURL);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(codeforcesURL, HttpMethod.GET, entity, String.class);
            Gson gson = new Gson();
            JsonObject codeforcesResponse = gson.fromJson(responseEntity.getBody(), JsonObject.class);
            if (Objects.isNull(codeforcesResponse)) {
                response.put(REASON, CODEFORCES_ID_FETCH_ERROR);
                return false;
            } else {
                JsonElement codeforcesProfileJson = codeforcesResponse.getAsJsonArray("result").get(0);
                CodeforcesProfile codeforcesProfile = gson.fromJson(codeforcesProfileJson, CodeforcesProfile.class);
                if(Objects.isNull(codeforcesProfile.getRating())){
                    user.setRating(INITIAL_RATING);
                    user.setMaxRating(INITIAL_RATING);
                }
                else {
                    //update user class with codeforces fetch values
                    int avgCodeforcesRating = ((codeforcesProfile.getRating() + codeforcesProfile.getMaxRating()) / 2);
                    int rating = (int) ((avgCodeforcesRating) / CODEFORCES_TO_CODEBATTLE_FACTOR);
                    user.setRating(rating);
                    user.setMaxRating(Math.max((int) (user.getMaxRating() / CODEFORCES_TO_CODEBATTLE_FACTOR), rating));
                }
                user.setCountry(codeforcesProfile.getCountry());
                user.setCity(codeforcesProfile.getCity());
                user.setOrganization(codeforcesProfile.getOrganization());
                user.setAvatar(codeforcesProfile.getAvatar());
                user.setTitlePhoto(codeforcesProfile.getTitlePhoto());

                LoginServiceImpl.logger.info("{}", codeforcesProfile);
                return true;
            }
        } catch (HttpStatusCodeException e) {
            String errorPayload = e.getResponseBodyAsString();
            Gson gson = new Gson();
            JsonObject codeforcesResponse = gson.fromJson(errorPayload, JsonObject.class);
            response.put(REASON, codeforcesResponse.get("comment").getAsString());
            LoginServiceImpl.logger.info("{}", response);
            return false;
        }
    }

    private boolean sendEmailVerification(User user, String email, Map<String, Object> response) {

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);
        logger.info("conformation token {}",confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("CODE BATTLE - Complete Registration!");
        mailMessage.setFrom("mohitagr00001@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + URI_CONFIRM_ACCOUNT + "?token=" + confirmationToken.getConfirmationToken());
        emailSenderService.sendEmail(mailMessage);
        user.setUserVerificationStatus(UserVerificationStatus.PENDING);
        return true;
    }

    private boolean regexMatcher(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        System.out.println(value + " : " + matcher.matches());
        return !matcher.matches();
    }
}
