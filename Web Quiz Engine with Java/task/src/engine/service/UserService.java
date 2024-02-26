package engine.service;

import engine.model.Quiz;
import engine.model.User;
import engine.repository.UserRepository;
import engine.security.UserAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByEmail(UserDetails userDetails) {
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername());
    }

    public void createUser(User user) {
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        encodeUserPassword(user);
        userRepository.save(user);
    }

    public void setUserForNewQuiz(UserDetails userDetails, Quiz quiz) {
        var user = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
        quiz.setUser(user.get());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return new UserAdapter(user);
    }

    private void encodeUserPassword(User user) {
        user.setPassword(new BCryptPasswordEncoder(9).encode(user.getPassword()));
    }
}
