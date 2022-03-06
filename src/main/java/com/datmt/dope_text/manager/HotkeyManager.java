package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.UserFile;
import com.datmt.dope_text.helper.Log1;
import com.datmt.dope_text.helper.TextSearcher;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
        final KeyCombination increaseSize = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN);
        final KeyCombination decreaseSize = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);


        if (save.match(ke)) {
            save(ke, scene);
        } else if (createNew.match(ke)) {
            createNew(ke, scene);
        } else if (close.match(ke)) {
            closeCurrentFile(ke, scene);
        } else if (find.match(ke)) {
            findInCurrentFile(ke, scene);
        } else if (export.match(ke)) {
            export(ke, scene);
        } else if (increaseSize.match(ke)) {
            increaseSize();
        } else if (decreaseSize.match(ke)) {
            decreaseSize();
        }
    }

    private static void export(KeyEvent ke, Scene scene) {
        ke.consume();
        if (StaticResource.currentFile != null && StaticResource.currentFile.getLocalPath() != null && Files.exists(Path.of(StaticResource.currentFile.getLocalPath()))) {
            Log1.logger.info("Already exported to file");
            return;
        }

        FileChooser fileChooser = new FileChooser();

        //Show save file dialog
        File file = fileChooser.showSaveDialog(scene.getWindow());

        if (file != null) {
            try {
                PrintWriter writer;
                writer = new PrintWriter(file);
                writer.println(StaticResource.codeArea.getText());
                writer.close();

                //update file path to db and the currently select file
                UserFile currentFile = CurrentFileManager.getFileFromListViewById(StaticResource.currentFile.getId());
                if (currentFile == null) {
                    Log1.logger.error("Current file in list view is null");
                    return;
                }
                currentFile.setLocalPath(file.getAbsolutePath());
                CurrentFileManager.updateFilePath(currentFile.getId(), file.getAbsolutePath());
            } catch (IOException | SQLException ex) {
                Log1.logger.error(ex);
            }
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
        CurrentFileManager.closeCurrentFile();
        ke.consume(); // <-- stops passing the event to next node
    }

    private static void createNew(KeyEvent ke, Scene scene) {
        ListView currentFiles = (ListView) scene.lookup("#currentFiles");

        try {
            if (StaticResource.currentFile != null)
                CurrentFileManager.saveFileBeforeSelectionChange(StaticResource.currentFile);
            DB db = new DB();

            UserFile f = db.createFile("", "new dope-text-" + UUID.randomUUID().toString().replace("-", "").substring(0, 5));

            currentFiles.getItems().add(f);
            currentFiles.getSelectionModel().select(f);
            StaticResource.allCurrentlyOpenFiles.add(f);
            CurrentFileManager.updateCurrentlyOpenedFile(f);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        ke.consume(); // <-- stops passing the event to next node
    }

    private static void save(KeyEvent ke, Scene scene) {

        if (StaticResource.currentFile != null) {
            StaticResource.currentFile.setContent(StaticResource.codeArea.getText());
            try {
                CurrentFileManager.saveCurrentFileToDB(StaticResource.codeArea.getText());
                CurrentFileManager.saveCurrentFileToDisk();
            } catch (SQLException ex) {
                Log1.logger.error(ex);
            }

        }

        ke.consume(); // <-- stops passing the event to next node
    }

    private static void increaseSize() {

        StaticResource.currentFontSize++;
        StaticResource.codeArea.setStyle("-fx-font-size: " + StaticResource.currentFontSize + "px;");
    }

    private static void decreaseSize() {
        if (StaticResource.currentFontSize > 1)
            StaticResource.currentFontSize--;
        StaticResource.codeArea.setStyle("-fx-font-size: " + StaticResource.currentFontSize + "px;");
    }
}
