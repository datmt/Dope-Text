package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.UserFile;
import com.datmt.dope_text.helper.TextSearcher;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.UUID;

public class HotkeyManager {
    private static Logger logger = LogManager.getLogger(HotkeyManager.class.getName());

    public static void manage(KeyEvent ke, Scene scene) {

        var os = System.getProperty("os.name");

        System.out.println("OS: " + os);
        var commandKey = KeyCombination.CONTROL_DOWN;

        if (os.contains("Mac")) {
            commandKey = KeyCombination.META_DOWN;
        }


        final KeyCombination save = new KeyCodeCombination(KeyCode.S, commandKey);
        final KeyCombination createNew = new KeyCodeCombination(KeyCode.N, commandKey);
        final KeyCombination close = new KeyCodeCombination(KeyCode.W, commandKey);
        final KeyCombination find = new KeyCodeCombination(KeyCode.F, commandKey);
        final KeyCombination export = new KeyCodeCombination(KeyCode.E, commandKey);
        final KeyCombination exportAs = new KeyCodeCombination(KeyCode.E, commandKey, KeyCombination.SHIFT_DOWN);
        final KeyCombination open = new KeyCodeCombination(KeyCode.O, commandKey);
        final KeyCombination quit = new KeyCodeCombination(KeyCode.Q, commandKey);
        final KeyCombination increaseSize = new KeyCodeCombination(KeyCode.EQUALS, commandKey);
        final KeyCombination decreaseSize = new KeyCodeCombination(KeyCode.MINUS, commandKey);


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
            increaseSize(ke);
        } else if (decreaseSize.match(ke)) {
            decreaseSize(ke);
        } else if (open.match(ke)) {
            try {
                open(scene, ke);
            } catch (SQLException | IOException e) {
                logger.error(e);
            }
        } else if (quit.match(ke)) {
            Platform.exit();
        }
    }

    private static void export(KeyEvent ke, Scene scene) {
        ke.consume();
        if (StaticResource.currentFile != null && StaticResource.currentFile.getLocalPath() != null && Files.exists(Path.of(StaticResource.currentFile.getLocalPath()))) {
            logger.info("Already exported to file");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        assert StaticResource.currentFile != null;
        fileChooser.setInitialFileName(StaticResource.currentFile.getFileName());

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
                    logger.error("Current file in list view is null");
                    return;
                }
                currentFile.setLocalPath(file.getAbsolutePath());
                CurrentFileManager.updateFilePath(currentFile.getId(), file.getAbsolutePath());
            } catch (IOException | SQLException ex) {
                logger.error(ex);
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
        if (StaticResource.currentFilesLV != null && StaticResource.currentFilesLV.getItems().size() > 0) {
            try {
                UserFile newSelectedFile = StaticResource.currentFilesLV.getItems().get(0);
                CurrentFileManager.updateCurrentlyOpenedFile(newSelectedFile);
                StaticResource.currentFilesLV.getSelectionModel().select(newSelectedFile);
            } catch (SQLException e) {
                logger.error("Exception when setting default selected file");
            }

        }

        ke.consume(); // <-- stops passing the event to next node
    }

    private static void createNew(KeyEvent ke, Scene scene) {

        try {
            if (StaticResource.currentFile != null)
                CurrentFileManager.saveFileBeforeSelectionChange(StaticResource.currentFile);
            DB db = DB.getInstance();

            UserFile f = db.createFile("", "new dope-text-" + UUID.randomUUID().toString().replace("-", "").substring(0, 5));

            StaticResource.currentFilesLV.getItems().add(f);
            StaticResource.currentFilesLV.getSelectionModel().select(f);
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
                CurrentFileManager.saveCurrentFileToDB(StaticResource.codeArea.getText(), StaticResource.currentFile.getId());
                CurrentFileManager.saveCurrentFileToDisk();
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }

        ke.consume(); // <-- stops passing the event to next node
    }

    private static void increaseSize(KeyEvent ke) {

        StaticResource.currentFontSize++;
        StaticResource.codeArea.setStyle("-fx-font-size: " + StaticResource.currentFontSize + "px;");
        ke.consume();
    }

    private static void decreaseSize(KeyEvent ke) {
        if (StaticResource.currentFontSize > 1)
            StaticResource.currentFontSize--;
        StaticResource.codeArea.setStyle("-fx-font-size: " + StaticResource.currentFontSize + "px;");
        ke.consume();

    }

    private static void open(Scene scene, KeyEvent ke) throws SQLException, IOException {
        FileChooser chooser = new FileChooser();

        File f = chooser.showOpenDialog(scene.getWindow());

        if (f != null) {
            DB db = DB.getInstance();
            UserFile existingFile = db.findFindByLocalPath(f.getAbsolutePath());

            if (existingFile == null) {
                UserFile newFile = db.createFile(Files.readString(f.toPath()), f.getName());
                db.updateLocalFilePath(newFile.getId(), f.getAbsolutePath());
                newFile.setLocalPath(f.getAbsolutePath());

                StaticResource.currentFilesLV.getItems().add(newFile);
                StaticResource.currentFilesLV.getSelectionModel().select(newFile);

                CurrentFileManager.updateCurrentlyOpenedFile(newFile);
                CurrentFileManager.selectCurrentFileById(newFile.getId());
            } else {
                CurrentFileManager.updateCurrentlyOpenedFile(existingFile);
                StaticResource.codeArea.replaceText(Files.readString(f.toPath()));
                CurrentFileManager.selectCurrentFileById(existingFile.getId());
            }
        }


        ke.consume();
    }
}
