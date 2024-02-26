package engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResult {
    public static final String CORRECT_ANSWER = "Congratulations, you're right!";
    public static final String WRONG_ANSWER = "Wrong answer! Please, try again.";

    private boolean success;
    private String feedback;
}
