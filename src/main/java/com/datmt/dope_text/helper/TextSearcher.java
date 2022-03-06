package com.datmt.dope_text.helper;

import com.datmt.dope_text.manager.StaticResource;

import java.util.ArrayList;
import java.util.List;

public class TextSearcher {


    public static void highlightMatchText(String text, String searchWord) {
        clearHighlight();
        List<Integer> startIndex = findWord(text, searchWord);

        if (startIndex.size() == 0) {
            System.out.println("No word found");
            return;
        }


        for (int id : startIndex) {
            StaticResource.codeArea.setStyleClass(id, id + searchWord.length(), "hlt");
        }
    }

    public static List<Integer> findWord(String textString, String word) {
        List<Integer> indexes = new ArrayList<>();
        String lowerCaseTextString = textString.toLowerCase();
        String lowerCaseWord = word.toLowerCase();

        int index = 0;
        while(index != -1){
            index = lowerCaseTextString.indexOf(lowerCaseWord, index);
            if (index != -1) {
                indexes.add(index);
                index++;
            }
        }
        return indexes;
    }

    public static void clearHighlight() {
        if (StaticResource.codeArea.getText().length() > 0)
            StaticResource.codeArea.clearStyle(0, StaticResource.codeArea.getText().length());
    }
}
