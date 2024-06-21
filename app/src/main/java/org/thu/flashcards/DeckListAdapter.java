package org.thu.flashcards;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class DeckListAdapter extends BaseAdapter {

    ArrayList<DeckEntry> data;
    public DeckListAdapter(ArrayList<DeckEntry> data) {

        this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        DeckEntry entry = data.get(position);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.deck_entry_item, null, false);
        }

        TextView name = convertView.findViewById(R.id.deck_name);
        name.setText(entry.name);

        Button btnStudy = convertView.findViewById(R.id.btn_study);
        Button btnEdit = convertView.findViewById(R.id.btn_edit);

        Button btnDelete = convertView.findViewById(R.id.btn_delete);
        Button btnStats = convertView.findViewById(R.id.btn_stats);

        btnEdit.setOnClickListener(v -> showEditDialog(position, context));

        btnStudy.setOnClickListener(v -> {
            if (entry.getFlashcards().isEmpty()) {
                new AlertDialog.Builder(context)
                        .setTitle("Deck is empty")
                        .setMessage("Please add flashcards first.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                entry.incrementStudyCount();
                entry.setLastStudyTime(System.currentTimeMillis());

                Intent intent = new Intent(context, FlashcardActivity.class);
                intent.putExtra("deck", entry);
                context.startActivity(intent);
            }
        });


        btnStats.setOnClickListener(v -> {
            int cardCount = entry.getFlashcards().size();
            int studyCount = entry.getStudyCount();
            long lastStudyTime = entry.getLastStudyTime();
            String lastStudyTimeStr = lastStudyTime == 0 ? "Never" : new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(lastStudyTime));

            String recommendation = "You should study again soon.";
            if (studyCount > 0) {
                switch (studyCount) {
                    case 1:  recommendation = "You should study again in 1 day";
                        break;
                    case 2:  recommendation = "You should study again in 3 days";
                        break;
                    case 3:  recommendation = "You should study again in 7 days";
                        break;
                    case 4:  recommendation = "You should study again in 15 days";
                        break;
                    case 5:   recommendation = "You should study again in 1 month";
                        break;
                    default:  recommendation = "You should study again in 3 months";
                }
            }

            String message =
                    "Number of flashcards: " + cardCount + "\n" +
                    "Number of times studied: " + studyCount + "\n" +
                    "Last studied: " + lastStudyTimeStr + "\n" +
                    recommendation;

            new AlertDialog.Builder(context)
                    .setTitle("Deck Statistics: " + entry.getName())
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Delete Deck")
                .setMessage("Are you sure you want to delete this deck?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    data.remove(position);
                    notifyDataSetChanged();
                    // Save to preferences
                    PreferencesUtil.saveDecks(context, data);
                    Toast.makeText(context, "Deck deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());

        return convertView;
    }
    private void showEditDialog(final int position, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Deck Name");

        final EditText input = new EditText(context);
        input.setHint("Enter new deck name");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                data.get(position).setName(newName);
                notifyDataSetChanged();
                // Save to preferences
                PreferencesUtil.saveDecks(context, DeckEntry.DeckEntries);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}

