package org.thu.flashcards;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class FlashcardActivity extends AppCompatActivity {

    private ArrayList<Flashcard> flashcards;
    private int currentIndex = 0;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView cardNumber;
    private Button nextButton;
    private DeckEntry deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        questionTextView = findViewById(R.id.questionTextView);
        answerTextView = findViewById(R.id.answerTextView);
        cardNumber = findViewById(R.id.text_card_number);
        nextButton = findViewById(R.id.nextButton);
        Button backButton = findViewById(R.id.backButton);

        nextButton.setBackgroundColor(getResources().getColor(R.color.purple));
        backButton.setBackgroundColor(getResources().getColor(R.color.purple));

        deck = (DeckEntry) getIntent().getSerializableExtra("deck");
        if (deck != null) {
            flashcards = deck.getFlashcards();
        }

        if (flashcards != null && !flashcards.isEmpty()) {
            displayFlashcard(currentIndex);
        }

        answerTextView.setOnClickListener(v -> revealAnswer());

        nextButton.setOnClickListener(v -> {
            if (currentIndex < flashcards.size() - 1) {
                currentIndex++;
                displayFlashcard(currentIndex);
            } else {
                showFinishConfirmation();
            }
            updateNextButton();
        });

        backButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayFlashcard(currentIndex);
            }
            updateNextButton();
        });

        updateNextButton();
    }

    private void displayFlashcard(int index) {
        Flashcard flashcard = flashcards.get(index);
        questionTextView.setText(flashcard.getQuestion());
        answerTextView.setText(R.string.tap_to_reveal);
        cardNumber.setText((currentIndex + 1) + " / " + flashcards.size());
    }

    private void revealAnswer() {
        Flashcard flashcard = flashcards.get(currentIndex);
        answerTextView.setText(flashcard.getAnswer());
    }

    private void updateNextButton() {
        if (currentIndex == flashcards.size() - 1) {
            nextButton.setText(R.string.finish_btn);
            nextButton.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else {
            nextButton.setText(R.string.next_btn);
            nextButton.setBackgroundColor(getResources().getColor(R.color.purple));
        }
    }

    private void showFinishConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Finish Study Session")
                .setMessage("Do you want to finish studying?")
                .setPositiveButton("Yes", (dialog, which) -> finishStudySession())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void finishStudySession() {
            finish();
        }
}
