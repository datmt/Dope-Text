package com.datmt.dope_text.manager;


import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.UserFile;
import com.datmt.dope_text.helper.FileHelper;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;

import java.sql.SQLException;

public class CurrentFileManager {
    private static Logger logger = LogManager.getLogger(CurrentFileManager.class.getName());
    
    public static void updateCurrentlyOpenedFile(UserFile file) throws SQLException {
        if (file == null || file.equals(StaticResource.currentFile)) {
            return;
        }

        CodeArea codeArea = StaticResource.codeArea;
        codeArea.replaceText(file.getContent());
        codeArea.scrollToPixel(0, 0);
        StaticResource.currentFile = file;

        DB db = DB.getInstance();
        db.updateLastOpenedFile(file.getId());

        if (StaticResource.stage != null) {
            StaticResource.stage.setTitle(file.getFileName() + " | Dope Text");
            StaticResource.currentFileTab.setText(file.getFileName());
        }

    }


    public static void saveFileBeforeSelectionChange(UserFile file) throws SQLException {

        logger.info("saving file before selection change: {}", file.getFileName());

        if (file.getContent().equals(StaticResource.codeArea.getText())) {
            logger.info("Content did not change. No saving needed");
            return;
        }

        file.setContent(null);
        file.setContent(StaticResource.codeArea.getText());
        saveCurrentFileToDB(StaticResource.codeArea.getText(), file.getId());
        saveCurrentFileToDisk();
    }

    public static void saveCurrentFileToDB(String content, Long fileId) throws SQLException{
        DB db = DB.getInstance();
        db.updateFileContent(fileId, content);
    }

    public static void saveCurrentFileToDisk() {
        UserFile file = StaticResource.currentFile;
        if (file == null || file.getLocalPath() == null) {
            logger.warn("File not saved: current file is null or local path is not set");
            return;
        }

        FileHelper.saveToDisk(file.getLocalPath(), StaticResource.codeArea.getText());
    }

    public static void updateFilePath(Long fileId, String filePath) throws SQLException {
        DB db = DB.getInstance();
        db.updateLocalFilePath(fileId, filePath);
    }


    public static UserFile getFileFromListViewById(Long id) {
        if (StaticResource.currentFilesLV == null) {
            logger.error("List view is null");
            return null;
        }
        return StaticResource.currentFilesLV.getItems().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public static UserFile getFileFromListViewById(Long id, ListView<UserFile> listView) {
        return listView.getItems().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public static void closeCurrentFile() {
        if (StaticResource.currentFilesLV == null || StaticResource.closedFilesLV == null) {
            logger.error("current List view or closed files list view is null ");
            return;
        }

        StaticResource.closedFilesLV.getItems().add(StaticResource.currentFilesLV .getSelectionModel().getSelectedItem());
        StaticResource.currentFilesLV .getItems().remove(StaticResource.currentFilesLV .getSelectionModel().getSelectedItem());


        try {
            DB db = DB.getInstance();
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
        if (file != null && StaticResource.closedFilesLV != null) {
            StaticResource.closedFilesLV.getSelectionModel().select(file);
        }
    }

    public static void selectCurrentFileById(Long id, ListView<UserFile> listView) {
        UserFile file = getFileFromListViewById(id, listView);
        if (file != null) {

            listView.getSelectionModel().select(file);
        }
    }
}
