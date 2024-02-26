package engine.controller;

import engine.model.*;
import engine.service.QuizService;
import engine.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class WebQuizController {
    private final QuizService quizService;
    private final UserService userService;

    public WebQuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    @GetMapping("/quizzes/{id}")
    public Optional<Quiz> findQuiz(@PathVariable(name = "id") Long quizId) {
        return quizService.findQuiz(quizId);
    }

    @GetMapping("/quizzes")
    public Page<Quiz> findQuizzes(@RequestParam(defaultValue = "0") Integer page) {
        return quizService.findQuizzes(page);
    }

    @GetMapping("/quizzes/completed")
    public Page<SolvedQuiz> findSolvedUserQuizzes(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "0") Integer page) {
        return quizService.findSolvedUserQuizzes(userDetails, page);
    }

    @PostMapping("/quizzes")
    public Quiz createQuiz(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Quiz quiz) {
        return quizService.createQuiz(userDetails, quiz);
    }

    @PostMapping("/quizzes/{id}/solve")
    public AnswerResult solveQuiz(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "id") Long quizId, @RequestBody UserAnswer answer) {
        return quizService.solveQuiz(userDetails, quizId, answer);
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity deleteQuiz(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return quizService.deleteQuiz(userDetails, id);
    }

    @PostMapping("/register")
    public void createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
    }
}
