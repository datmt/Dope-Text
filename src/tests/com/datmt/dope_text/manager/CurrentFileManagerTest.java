package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.model.UserFile;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrentFileManagerTest {

    ListView<UserFile> currentFilesLV;
    @BeforeAll
    public void setup() {
        currentFilesLV = new ListView<>();


    }


    @Test
    void updateCurrentlyOpenedFile() {
    }

    @Test
    void saveFileBeforeSelectionChange() {
    }

    @Test
    void saveCurrentFileToDB() {
    }

    @Test
    void saveCurrentFileToDisk() {
    }

    @Test
    void updateFilePath() {
    }

    @Test
    void exportToLocalFile() {
    }

    @Test
    void openLocalFile() {
    }

    @Test
    void getFileFromListViewById() {
    }

    @Test
    void testGetFileFromListViewById() {
    }

    @Test
    void closeCurrentFile() {
    }

    @Test
    void selectCurrentFileById() {
    }

    @Test
    void testSelectCurrentFileById() {
    }
}