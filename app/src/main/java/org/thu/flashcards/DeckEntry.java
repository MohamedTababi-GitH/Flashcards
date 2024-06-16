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
    int studyCount;
    long lastStudyTime;
    public DeckEntry(String name){
        this.flashcards = new ArrayList<>();
        this.name=name;
        this.studyCount = 0;
        this.lastStudyTime = 0;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void incrementStudyCount() {
        studyCount = studyCount + 1;
    }

    public long getLastStudyTime() {
        return lastStudyTime;
    }

    public void setLastStudyTime(long lastStudyTime) {
        this.lastStudyTime = lastStudyTime;
    }



    public ArrayList<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void addFlashcard(Flashcard flashcard) {
        flashcards.add(flashcard);}
}
