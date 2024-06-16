package org.thu.flashcards;

import java.io.Serializable;

public class Flashcard implements Serializable {
    String question;
    String answer;

    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }

}

