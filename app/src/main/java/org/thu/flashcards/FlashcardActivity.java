package org.thu.flashcards;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FlashcardActivity extends AppCompatActivity {

    private ArrayList<Flashcard> flashcards;
    private int currentIndex = 0;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        questionTextView = findViewById(R.id.questionTextView);
        answerTextView = findViewById(R.id.answerTextView);
        cardNumber = findViewById(R.id.text_card_number);
        Button nextButton = findViewById(R.id.nextButton);
        Button backButton = findViewById(R.id.backButton);

        DeckEntry deck = (DeckEntry) getIntent().getSerializableExtra("deck");

        if (deck != null) {
            flashcards = deck.getFlashcards();
        }

        if (flashcards != null && !flashcards.isEmpty()) {
            displayFlashcard(currentIndex);
        }
        // Set up click listener for answerTextView
        answerTextView.setOnClickListener(v -> {
            // Display the actual answer when tapped
            revealAnswer();
        });

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
        answerTextView.setText(R.string.tap_to_reveal);
        cardNumber.setText((currentIndex+1)+ " / " + flashcards.size() );
    }
    private void revealAnswer() {
        // Get the current flashcard
        Flashcard flashcard = flashcards.get(currentIndex);
        // Set the answerTextView text to the actual answer
        answerTextView.setText(flashcard.getAnswer());
    }

}
