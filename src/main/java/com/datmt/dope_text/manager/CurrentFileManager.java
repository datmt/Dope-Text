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
            Log1.logger.warn("File not saved: current file is null or local path is not set");
            return;
        }

        FileHelper.saveToDisk(file.getLocalPath(), StaticResource.codeArea.getText());
    }

    public static void updateFilePath(Long fileId, String filePath) throws SQLException {
        DB db = new DB();
        db.updateLocalFilePath(fileId, filePath);
    }



    public static void exportToLocalFile() {

    }

    public static void openLocalFile() {

    }

    public static UserFile getFileFromListViewById(Long id) {
        if (StaticResource.currentFilesLV == null) {
            Log1.logger.error("List view is null");
            return null;
        }
        return StaticResource.currentFilesLV.getItems().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public static UserFile getFileFromListViewById(Long id, ListView<UserFile> listView) {
        return listView.getItems().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public static void closeCurrentFile() {
        if (StaticResource.currentFilesLV == null || StaticResource.closedFilesLV == null) {
            Log1.logger.error("List view is null");
            return;
        }

        StaticResource.closedFilesLV.getItems().add(StaticResource.currentFilesLV .getSelectionModel().getSelectedItem());
        StaticResource.currentFilesLV .getItems().remove(StaticResource.currentFilesLV .getSelectionModel().getSelectedItem());

        UserFile userFileInStaticList = StaticResource.allCurrentlyOpenFiles.stream().filter(t -> t.getId().equals(StaticResource.currentFile.getId())).findFirst().orElse(null);

        if (userFileInStaticList!= null) {
            StaticResource.allCurrentlyOpenFiles.remove(userFileInStaticList);
        }

        try {
            DB db = new DB();
            db.updateFileOpenStatus(StaticResource.currentFile.getId(), 0);

            if (StaticResource.currentFilesLV .getItems().size() > 0)
                CurrentFileManager.updateCurrentlyOpenedFile(StaticResource.currentFilesLV .getItems().get(0));
            else
                StaticResource.codeArea.replaceText("");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void selectCurrentFileById(Long id) {
        UserFile file = getFileFromListViewById(id);
        if (file != null) {
            ListView<UserFile> currentFiles = (ListView<UserFile>) StaticResource.scene.lookup("#currentFilesLV");
            currentFiles.getSelectionModel().select(file);
        }
    }

    public static void selectCurrentFileById(Long id, ListView<UserFile> listView) {
        UserFile file = getFileFromListViewById(id, listView);
        if (file != null) {

            listView.getSelectionModel().select(file);
        }
    }
}
