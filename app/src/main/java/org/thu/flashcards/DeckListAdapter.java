package org.thu.flashcards;

import android.content.Context;
import android.content.DialogInterface;
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

        this.data = DeckEntry.DeckEntries;
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
        // Get data
        DeckEntry entry = data.get(position);


        if (convertView == null){
            // Create UI views from layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.deck_entry_item, null, false);
        }

        TextView name = convertView.findViewById(R.id.deck_name);
        //rate.setText("rate: " + String.valueOf(entry.exchangeRate));
        name.setText(entry.name);

        Button btnStudy = convertView.findViewById(R.id.btn_study);
        btnStudy.setText("Study");
        Button btnEdit = convertView.findViewById(R.id.btn_edit);
        btnEdit.setText("Edit");
        Button btnDelete = convertView.findViewById(R.id.btn_delete);
        btnDelete.setText("Delete");

        btnEdit.setOnClickListener(v -> showEditDialog(position, context));

        btnStudy.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlashcardActivity.class);
            intent.putExtra("deck", entry);
            context.startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            // Handle delete button click here
            data.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Deck deleted", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
    private void showEditDialog(final int position, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Deck Name");

        final EditText input = new EditText(context);
        input.setHint("Enter new deck name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                data.get(position).setName(newName);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}

