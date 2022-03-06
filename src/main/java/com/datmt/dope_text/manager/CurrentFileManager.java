package com.datmt.dope_text.manager;


import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.UserFile;
import com.datmt.dope_text.helper.FileHelper;
import com.datmt.dope_text.helper.Log1;
import javafx.scene.control.ListView;
import org.fxmisc.richtext.CodeArea;

import java.sql.SQLException;

public class CurrentFileManager {
    public static void updateCurrentlyOpenedFile(UserFile file) throws SQLException {
        if (file == null || file.equals(StaticResource.currentFile)) {
            return;
        }

        CodeArea codeArea = StaticResource.codeArea;
        codeArea.replaceText(file.getContent());
        codeArea.scrollToPixel(0, 0);
        StaticResource.currentFile = file;

        DB db = new DB();
        db.updateLastOpenedFile(file.getId());

        if (StaticResource.stage != null) {
            StaticResource.stage.setTitle(file.getFileName() + " | Dope Text");
        }


    }

    public static void saveFileBeforeSelectionChange(UserFile file) throws SQLException {

        if (file.getContent().equals(StaticResource.codeArea.getText()))
            return;

        saveCurrentFileToDB(StaticResource.codeArea.getText());
        saveCurrentFileToDisk();
    }

    public static void saveCurrentFileToDB(String content) throws SQLException{
        UserFile file = StaticResource.currentFile;
        if (file == null) {
            Log1.logger.error("current file is null");
            return;
        }
        DB db = new DB();
        file.setContent(content);
        db.updateFileContent(file.getId(), content);
    }

    public static void saveCurrentFileToDisk() {
        UserFile file = StaticResource.currentFile;
        if (file == null || file.getLocalPath() == null) {
            Log1.logger.error("current file is null or local path is not set");
            return;
        }

        FileHelper.saveToDisk(file.getLocalPath(), StaticResource.codeArea.getText());
    }

    public static void updateFilePath(Long fileId, String filePath) throws SQLException {
        DB db = new DB();
        db.updateFilePath(fileId, filePath);
    }



    public static void exportToLocalFile() {

    }

    public static void openLocalFile() {

    }

    public static UserFile getFileFromListViewById(Long id) {
        ListView<UserFile> currentFiles = (ListView<UserFile>) StaticResource.scene.lookup("#currentFiles");
        return currentFiles.getItems().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }
}
