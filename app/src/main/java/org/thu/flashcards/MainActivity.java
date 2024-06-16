package org.thu.flashcards;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class MainActivity extends AppCompatActivity {
    Dialogue dialogue = new Dialogue();
    private ActivityResultLauncher<Intent> pickJsonFileLauncher;
    DeckListAdapter adapter;
    Context context;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DeckEntry firstEntry = new DeckEntry("first deck");
        DeckEntry.DeckEntries.add(firstEntry);
        firstEntry.addFlashcard(new Flashcard("first","one"));
        firstEntry.addFlashcard(new Flashcard("first","one"));
        firstEntry.addFlashcard(new Flashcard("first","one"));

        adapter = new DeckListAdapter(DeckEntry.DeckEntries);
        ListView listView = findViewById(R.id.deck_list);
        listView.setAdapter(adapter);

        Button clearAllBtn = findViewById(R.id.clear_all_btn);
        clearAllBtn.setOnClickListener(view -> dialogue.showClearListConfirmationDialog(adapter, this));

        // Fab declaration and Onclick method
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String htmlText = "<i>-Use this app to create simple flashcards to help you study better.</i><br>" +
                    "<br>Here is a simple tutorial:" +
                    "<ol type=\"1\">" +
                    "<li>Using the \"+\" button, add or import a new deck (only json files).</li>" +
                    "<li>Add flashcards to the deck by specifying a question and an answer.</li>" +
                    "<li>Once a deck is added, click the study button to start studying.</li>" +
                    "<li>Try to answer the question then click the answer to reveal it and check if you answered correctly." +
                    "<li>Once you reach the last card in the deck you can finish studying by pressing \"finish\". </li>" +
                    "<li>Check your deck stats by clicking the stats button.</li>"+
                    "<li>Using the share button, you can also share your study decks with everyone (as text or json files).</li>"+
                    "</ol>";

            Spanned textBody =  Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
            builder.setTitle("Welcome to Flashcards!");
            builder.setMessage(textBody);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        // Register the ActivityResultLauncher
        pickJsonFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        importDeckFromJson(uri);
                    }
                }
            }
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
            dialogue.showAddDeckDialog(adapter, this);
        }
        else if (item.getItemId() == R.id.action_share) {
            showSelectDeckDialogToShare();
        } else if (item.getItemId() == R.id.menu_import_deck) {
            openFileChooser();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        StringBuilder deckText = new StringBuilder();
        deckText.append("Deck Name: ").append(selectedDeck.getName()).append("\n\n");
        for (Flashcard flashcard : selectedDeck.getFlashcards()) {
            deckText.append("Question: ").append(flashcard.getQuestion()).append("\n");
            deckText.append("Answer: ").append(flashcard.getAnswer()).append("\n\n");
        }
        File file = new File(getExternalFilesDir(null), "deck.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(deckText.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri fileUri = FileProvider.getUriForFile(this, "org.thu.flashcards.fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Deck"));
    }
    private void exportDeckAsJson(DeckEntry selectedDeck) {
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
        File file = new File(getExternalFilesDir(null), "deck.json");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(deckJson.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri fileUri = FileProvider.getUriForFile(this, "org.thu.flashcards.fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/json");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Deck"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }}

    private void openFileChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/json");
            pickJsonFileLauncher.launch(intent);
        }
    }

    private void importDeckFromJson(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                String jsonString = stringBuilder.toString();

                JSONArray jsonArray = new JSONArray(jsonString);

                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String deckName = jsonObject.getJSONObject("deck").getString("name");
                JSONArray cardsArray = jsonObject.getJSONArray("cardList");

                DeckEntry newDeck = new DeckEntry(deckName);
                for (int i = 0; i < cardsArray.length(); i++) {
                    JSONObject cardObject = cardsArray.getJSONObject(i);
                    String question = cardObject.getString("question");
                    String answer = cardObject.getString("answer");
                    newDeck.addFlashcard(new Flashcard(question, answer));
                }
                DeckEntry.DeckEntries.add(newDeck);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Deck imported successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to import deck: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}


