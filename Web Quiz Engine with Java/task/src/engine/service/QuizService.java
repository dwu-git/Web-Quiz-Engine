package engine.service;

import engine.model.AnswerResult;
import engine.model.Quiz;
import engine.model.SolvedQuiz;
import engine.model.UserAnswer;
import engine.repository.QuizRepository;
import engine.repository.SolvedQuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final SolvedQuizRepository solvedQuizRepository;
    private final UserService userService;

    public QuizService(QuizRepository quizRepository, SolvedQuizRepository solvedQuizRepository, UserService userService) {
        this.quizRepository = quizRepository;
        this.solvedQuizRepository = solvedQuizRepository;
        this.userService = userService;
    }

    public Optional<Quiz> findQuiz(Long quizId) {
        if (quizRepository.findById(quizId).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        else
            return quizRepository.findById(quizId);
    }

    public Page<Quiz> findQuizzes(Integer pageNumber) {
        Page<Quiz> quizzes = quizRepository.findAll(PageRequest.of(pageNumber, 10));
        return quizzes;
    }

    public Page<SolvedQuiz> findSolvedUserQuizzes(UserDetails userDetails, Integer pageNumber) {
        Long userId = userService.findUserByEmail(userDetails).get().getId();
        Page<SolvedQuiz> solvedUserQuizzes = solvedQuizRepository.findAllByUserId(userId, PageRequest.of(pageNumber, 10, Sort.by("completedAt").descending()));
        return solvedUserQuizzes;
    }

    public Quiz createQuiz(UserDetails userDetails, Quiz quiz) {
        userService.setUserForNewQuiz(userDetails, quiz);
        quizRepository.save(quiz);
        return quiz;
    }

    public AnswerResult solveQuiz(UserDetails userDetails, Long quizId, UserAnswer userAnswer) {
        var quiz = findQuiz(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (isCorrectUserAnswer(userAnswer, quiz)) {
            var user = userService.findUserByEmail(userDetails).get();
            var solvedQuiz = solvedQuizRepository
                    .save(new SolvedQuiz(solvedQuizRepository.count() + 1, quizId, quiz, LocalDateTime.now(), user));
            user.getSolvedUserQuizzes().add(solvedQuiz);
            quiz.getSolvedQuizzes().add(solvedQuiz);
            return new AnswerResult(true, AnswerResult.CORRECT_ANSWER);
        }
        return new AnswerResult(false, AnswerResult.WRONG_ANSWER);
    }

    public ResponseEntity deleteQuiz(UserDetails userDetails, Long id) {
        var quizToDelete = findQuiz(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!userDetails.getUsername().equals(quizToDelete.getUser().getEmail()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        quizRepository.delete(quizToDelete);
        return ResponseEntity.noContent().build();
    }

    private boolean isCorrectUserAnswer(UserAnswer userAnswer, Quiz existedQuiz) {
        return (new HashSet<>(existedQuiz.getAnswer()).equals(userAnswer.getAnswer()));
    }
}

