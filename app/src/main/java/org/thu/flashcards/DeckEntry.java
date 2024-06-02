package org.thu.flashcards;

import java.io.Serializable;
import java.util.ArrayList;

public class DeckEntry implements Serializable {
    public static ArrayList<DeckEntry> DeckEntries = new ArrayList<>();
    private ArrayList<Flashcard> flashcards;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;

    public DeckEntry(String name){
        this.flashcards = new ArrayList<>();
        this.name=name;
    }

    public ArrayList<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void addFlashcard(Flashcard flashcard) {
        flashcards.add(flashcard);}
}
