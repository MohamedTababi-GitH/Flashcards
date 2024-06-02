package org.thu.flashcards;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FlashcardActivity extends AppCompatActivity {

    private ArrayList<Flashcard> flashcards;
    private int currentIndex = 0;
    private TextView questionTextView;
    private TextView answerTextView;
    private Button nextButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        questionTextView = findViewById(R.id.questionTextView);
        answerTextView = findViewById(R.id.answerTextView);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        DeckEntry deck = (DeckEntry) getIntent().getSerializableExtra("deck");

        if (deck != null) {
            flashcards = deck.getFlashcards();
        }

        if (flashcards != null && !flashcards.isEmpty()) {
            displayFlashcard(currentIndex);
        }

        nextButton.setOnClickListener(v -> {
            if (currentIndex < flashcards.size() - 1) {
                currentIndex++;
                displayFlashcard(currentIndex);
            }
        });

        backButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayFlashcard(currentIndex);
            }
        });
    }

    private void displayFlashcard(int index) {
        Flashcard flashcard = flashcards.get(index);
        questionTextView.setText(flashcard.getQuestion());
        answerTextView.setText(flashcard.getAnswer());
    }


}
