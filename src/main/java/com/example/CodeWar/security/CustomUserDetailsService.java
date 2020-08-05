package com.example.CodeWar.security;

import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.UserRepository;
//import com.example.polls.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String codeBattleId)
            throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = userRepository.findByCodeBattleId(codeBattleId);
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException("User not found with username or email : " + codeBattleId);
        }
//                orElseThrow(() ->
//                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
//        );
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserByCodeBattleId(String codeBattleId) {
        User user = userRepository.findByCodeBattleId(codeBattleId);
        if(Objects.isNull(user)){
            System.out.println("User not found in CustomUserDetailsService");
        }
        return UserPrincipal.create(user);
    }
}