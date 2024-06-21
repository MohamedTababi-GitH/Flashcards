package org.thu.flashcards;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PreferencesUtil {
    private static final String PREFS_NAME = "FlashcardsPrefs";
    private static final String DECKS_KEY = "decks";

    public static void saveDecks(Context context, ArrayList<DeckEntry> decks) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray jsonArray = new JSONArray();
        for (DeckEntry deck : decks) {
            try {
                JSONObject deckJson = new JSONObject();
                deckJson.put("name", deck.getName());

                JSONArray cardsArray = new JSONArray();
                for (Flashcard flashcard : deck.getFlashcards()) {
                    JSONObject cardJson = new JSONObject();
                    cardJson.put("question", flashcard.getQuestion());
                    cardJson.put("answer", flashcard.getAnswer());
                    cardsArray.put(cardJson);
                }
                deckJson.put("cards", cardsArray);
                jsonArray.put(deckJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString(DECKS_KEY, jsonArray.toString());
        editor.apply();
    }

    public static ArrayList<DeckEntry> loadDecks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(DECKS_KEY, null);
        ArrayList<DeckEntry> decks = new ArrayList<>();

        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject deckJson = jsonArray.getJSONObject(i);
                    String deckName = deckJson.getString("name");

                    DeckEntry deck = new DeckEntry(deckName);
                    JSONArray cardsArray = deckJson.getJSONArray("cards");

                    for (int j = 0; j < cardsArray.length(); j++) {
                        JSONObject cardJson = cardsArray.getJSONObject(j);
                        String question = cardJson.getString("question");
                        String answer = cardJson.getString("answer");
                        deck.addFlashcard(new Flashcard(question, answer));
                    }
                    decks.add(deck);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return decks;
    }
}
