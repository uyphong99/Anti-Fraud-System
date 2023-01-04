package antifraud.controller;

import antifraud.controller.json.ChangeRole;
import antifraud.controller.json.SwitchLock;
import antifraud.model.User;
import antifraud.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class RegistrationController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/api/auth/user")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {

        userDetailsService.addUser(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<User>> getUserRepo() {
        return new ResponseEntity<>(userDetailsService.getUserList(), HttpStatus.OK);
    }


    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Map delResponse = userDetailsService.remove(username);

        if (!delResponse.isEmpty()) {
            return new ResponseEntity<>(Map.of("username", username, "status", "Deleted successfully!")
                    , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /*
     * Lock or unlock user given String username
     */
    @PutMapping("/api/auth/access")
    public ResponseEntity<?> lockSwitcher(@RequestBody SwitchLock switchLock){
        String username = switchLock.getUsername();
        String operation = switchLock.getOperation();
        Map lockUnlockUser = userDetailsService.switchLock(username, operation);
        return new ResponseEntity<>(lockUnlockUser, HttpStatus.OK);
    }

    /*
     * Change user roles, receive JSON body
     */
    @PutMapping("/api/auth/role")
    public ResponseEntity<?> changeUserRole(@RequestBody ChangeRole userChanged){
        var username = userChanged.getUsername();
        var newRole = userChanged.getRole();

        if (!(newRole.equals("MERCHANT") || newRole.equals("SUPPORT"))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        var user = userDetailsService.changeUserRole(username, newRole);

        userDetailsService.remove(username);
        userDetailsService.getUserRepo().save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
