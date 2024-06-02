package org.thu.flashcards;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    DeckListAdapter adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DeckEntry.DeckEntries.add(new DeckEntry("first deck"));
        /*DeckEntry.DeckEntries.add(new DeckEntry("second deck"));
        DeckEntry.DeckEntries.add(new DeckEntry("third deck"));
        DeckEntry.DeckEntries.add(new DeckEntry("fourth deck"));
        DeckEntry.DeckEntries.add(new DeckEntry("fifth deck"));*/

        adapter = new DeckListAdapter(DeckEntry.DeckEntries);
        ListView listView = (ListView)findViewById(R.id.deck_list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_card) {
            showSelectDeckDialog();
            return true;
        } else if (item.getItemId() == R.id.menu_add_deck) {
            showAddDeckDialog();
        }
        else if (item.getItemId() == R.id.menu_clear_list) {
            showClearListConfirmationDialog();
        }

        return super.onOptionsItemSelected(item);

    }
    private void showAddDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Deck");
        builder.setMessage("Enter the deck name:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                DeckEntry.DeckEntries.add(new DeckEntry(deckName));
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showClearListConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear List");
        builder.setMessage("Are you sure you want to clear all decks");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Clear the list
            DeckEntry.DeckEntries.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(context, "list cleared", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss(); // Dismiss the dialog
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showSelectDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Deck");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        for (DeckEntry deck : DeckEntry.DeckEntries) {
            arrayAdapter.add(deck.getName());
        }

        builder.setAdapter(arrayAdapter, (dialog, which) -> {
            String deckName = arrayAdapter.getItem(which);
            showAddCardDialog(deckName);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showAddCardDialog(final String deckName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Card to " + deckName);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_card, findViewById(android.R.id.content), false);
        final EditText inputQuestion = viewInflated.findViewById(R.id.input_question);
        final EditText inputAnswer = viewInflated.findViewById(R.id.input_answer);

        builder.setView(viewInflated);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String question = inputQuestion.getText().toString().trim();
            String answer = inputAnswer.getText().toString().trim();
            if (!question.isEmpty() && !answer.isEmpty()) {
                for (DeckEntry deck : DeckEntry.DeckEntries) {
                    if (deck.getName().equals(deckName)) {
                        deck.addFlashcard(new Flashcard(question, answer));

                        adapter.notifyDataSetChanged();
                        Toast.makeText(context, "Card added to " + deckName, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } else {
                Toast.makeText(context, "Question and Answer cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

