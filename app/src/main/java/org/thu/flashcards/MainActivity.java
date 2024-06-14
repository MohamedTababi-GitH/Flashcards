package org.thu.flashcards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        DeckEntry firstEntry = new DeckEntry("first deck");
        DeckEntry.DeckEntries.add(firstEntry);
        adapter = new DeckListAdapter(DeckEntry.DeckEntries);
        ListView listView = (ListView)findViewById(R.id.deck_list);
        listView.setAdapter(adapter);

        Button clearAllBtn = findViewById(R.id.clear_all_btn);

        clearAllBtn.setOnClickListener(view ->{
            showClearListConfirmationDialog();
        });

        // Fab decleration and Onclick method
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String htmlText = "<h1>hello</h1>" ;
            Spanned textBody =  Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
            builder.setTitle("Tutorial");
            builder.setMessage(textBody);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
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
        else if (item.getItemId() == R.id.action_share) {
            showSelectDeckDialogToShare();
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

    private void showSelectDeckDialogToShare() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Deck to Share");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        for (DeckEntry deck : DeckEntry.DeckEntries) {
            arrayAdapter.add(deck.getName());
        }

        builder.setAdapter(arrayAdapter, (dialog, which) -> {
            String deckName = arrayAdapter.getItem(which);
            DeckEntry selectedDeck = null;
            for (DeckEntry deck : DeckEntry.DeckEntries) {
                if (deck.getName().equals(deckName)) {
                    selectedDeck = deck;
                    break;
                }
            }
            if (selectedDeck != null) {
                showExportFormatDialog(selectedDeck);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showExportFormatDialog(final DeckEntry selectedDeck) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Export Format");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Text");
        arrayAdapter.add("JSON");

        builder.setAdapter(arrayAdapter, (dialog, which) -> {
            String format = arrayAdapter.getItem(which);
            if ("Text".equals(format)) {
                exportDeckAsText(selectedDeck);
            } else if ("JSON".equals(format)) {
                exportDeckAsJson(selectedDeck);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void exportDeckAsText(DeckEntry selectedDeck) {
        // Create text content
        StringBuilder deckText = new StringBuilder();
        deckText.append("Deck Name: ").append(selectedDeck.getName()).append("\n\n");
        for (Flashcard flashcard : selectedDeck.getFlashcards()) {
            deckText.append("Question: ").append(flashcard.getQuestion()).append("\n");
            deckText.append("Answer: ").append(flashcard.getAnswer()).append("\n\n");
        }

        // Save the text content to a file
        File file = new File(getExternalFilesDir(null), "deck.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(deckText.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Get URI for the file using FileProvider
        Uri fileUri = FileProvider.getUriForFile(this, "org.thu.flashcards.fileprovider", file);

        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Deck"));
    }
    private void exportDeckAsJson(DeckEntry selectedDeck) {
        // Create JSON content
        JSONObject deckJson = new JSONObject();
        try {
            deckJson.put("deck_name", selectedDeck.getName());
            JSONArray cardsArray = new JSONArray();
            for (Flashcard flashcard : selectedDeck.getFlashcards()) {
                JSONObject cardJson = new JSONObject();
                cardJson.put("question", flashcard.getQuestion());
                cardJson.put("answer", flashcard.getAnswer());
                cardsArray.put(cardJson);
            }
            deckJson.put("cards", cardsArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Save the JSON content to a file
        File file = new File(getExternalFilesDir(null), "deck.json");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(deckJson.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Get URI for the file using FileProvider
        Uri fileUri = FileProvider.getUriForFile(this, "org.thu.flashcards.fileprovider", file);

        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/json");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Deck"));
    }
}

