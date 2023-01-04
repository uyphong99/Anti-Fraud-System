package antifraud.service;


import antifraud.exception.BadRequestException;
import antifraud.exception.UserAlreadyExistException;
import antifraud.model.User;
import antifraud.repository.UserRepository;
import antifraud.security.UserDetailsImpl;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;


    private final PasswordEncoder encoder;

    /*
    * The returned user will have ROLE_ prefix in role field
    * */
    public Optional<User> findUser(String username) throws UsernameNotFoundException {
        return userRepo.findUserEntityByUsernameIgnoreCase(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = findUser(username);

        UserDetailsImpl returnUser = user.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found " + username));

        return returnUser;
    }

    public void addUser(User user) {
        if (findUser(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistException();
        }
        user.setPassword(encoder.encode(user.getPassword()));

        if (userRepo.findAll().isEmpty()) {
            user.setRole("ADMINISTRATOR");
            user.setNonLocked(true);
        } else {
            user.setRole("MERCHANT");
            user.setNonLocked(false);
        }

        userRepo.save(user);
    }

    public Map<String, String> remove(String username) {
        Optional<User> delUser = findUser(username);
        Map<String, String> responseDel = new HashMap<>();

        if (delUser.isPresent()) {
            userRepo.delete(delUser.get());
            responseDel.put("username", username);
            responseDel.put("status", "Deleted successfully!");
        }
        return responseDel;
    }

    public List<User> getUserList() {
        return new ArrayList<>(userRepo.findAll());
    }

    public User changeUserRole(String username, String newRole){
        var user = findUser(username).get();

        user.setRole(newRole);

        return user;
    }

    public Map switchLock(String username, String operation) {
        var user = findUser(username).get();
        String lockState;

        if (user.getRole().equals("ADMINISTRATOR") && operation.equals("LOCK")) {
            throw new BadRequestException();
        }

        if (operation.equals("LOCK")) {
            user.setNonLocked(false);
            lockState = "locked";
        } else {
            user.setNonLocked(true);
            lockState = "unlocked";
        }

        userRepo.save(user);

        return Map.of("status", "User " + username + " " + lockState + "!" );
    }

    public UserRepository getUserRepo(){
        return this.userRepo;
    }

}