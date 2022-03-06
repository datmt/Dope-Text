package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.File;
import com.datmt.dope_text.helper.TextSearcher;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;

import java.sql.SQLException;
import java.util.UUID;

public class HotkeyManager {
    public static void manage(KeyEvent ke, Scene scene) {
        final KeyCombination save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        final KeyCombination createNew = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        final KeyCombination close = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
        final KeyCombination find = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        final KeyCombination export = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
        final KeyCombination exportAs = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        final KeyCombination open = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);


        if (save.match(ke)) {
            save(ke, scene);
        } else if (createNew.match(ke)) {
            createNew(ke, scene);
        } else if (close.match(ke)) {
            closeCurrentFile(ke, scene);
        } else if (find.match(ke)) {
            findInCurrentFile(ke, scene);
        }
    }

    private static void findInCurrentFile(KeyEvent ke, Scene scene) {
        String selectedText = StaticResource.codeArea.getSelectedText();

        TextField searchTF = (TextField) scene.lookup("#searchTF");

        if (selectedText != null && !selectedText.equals("")) {
            searchTF.setText(selectedText);
            TextSearcher.highlightMatchText(StaticResource.codeArea.getText(), selectedText);
            searchTF.requestFocus();
        } else {
            searchTF.requestFocus();
        }


        ke.consume(); // <-- stops passing the event to next node
    }

    private static void closeCurrentFile(KeyEvent ke, Scene scene) {
        ListView<File> currentFiles = (ListView) scene.lookup("#currentFiles");
        currentFiles.getItems().remove(currentFiles.getSelectionModel().getSelectedItem());
        try {
            DB db = new DB();
            db.updateFileOpenStatus(StaticResource.currentFile.getId(), 0);

            if (currentFiles.getItems().size() > 0)
                CurrentFileManager.updateCurrentlyOpenedFile(currentFiles.getItems().get(0));
            else
                StaticResource.codeArea.replaceText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        ke.consume(); // <-- stops passing the event to next node
    }

    private static void createNew(KeyEvent ke, Scene scene) {
        ListView currentFiles = (ListView) scene.lookup("#currentFiles");

        try {
            if (StaticResource.currentFile != null)
                CurrentFileManager.saveFileBeforeSelectionChange(StaticResource.currentFile);
            DB db = new DB();

            File f = db.createFile("", "new dope-text-" + UUID.randomUUID().toString().replace("-", "").substring(0, 5));

            currentFiles.getItems().add(f);
            currentFiles.getSelectionModel().select(f);
            CurrentFileManager.updateCurrentlyOpenedFile(f);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        ke.consume(); // <-- stops passing the event to next node
    }

    private static void save(KeyEvent ke, Scene scene) {
        CodeArea codeArea = (CodeArea) scene.lookup("#codeArea");

        if (StaticResource.currentFile != null) {
            StaticResource.currentFile.setContent(codeArea.getText());
            try {
                DB db = new DB();
                db.updateFile(StaticResource.currentFile.getId(), codeArea.getText());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

        ke.consume(); // <-- stops passing the event to next node
    }
}
