package org.thu.flashcards;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class Dialogue {

    public void showAddDeckDialog(DeckListAdapter adapter, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Deck");
        builder.setMessage("Enter the deck name:");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                DeckEntry.DeckEntries.add(new DeckEntry(deckName));
                adapter.notifyDataSetChanged();
                // Save to preferences
                PreferencesUtil.saveDecks(context, DeckEntry.DeckEntries);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showClearListConfirmationDialog(DeckListAdapter adapter, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

}
