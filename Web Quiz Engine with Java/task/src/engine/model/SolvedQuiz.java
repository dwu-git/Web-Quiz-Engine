package engine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolvedQuiz {
    @Id
    @JsonIgnore
    private Long solvedQuizId;

    @JsonProperty("id")
    private Long quizId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "quiz")
    private Quiz quiz;

    private LocalDateTime completedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;
}
