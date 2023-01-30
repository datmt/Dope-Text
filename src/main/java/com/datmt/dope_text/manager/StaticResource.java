package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.model.UserFile;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

public class StaticResource {
    public static CodeArea codeArea;
    public static UserFile currentFile;
    public static Scene scene;
    public static Stage stage;
    public static ListView<UserFile> currentFilesLV;
    public static ListView<UserFile> closedFilesLV;
    public static int currentFontSize = 13;
    public static Tab currentFileTab;

    public static CodeArea getCodeArea() {
        return codeArea;
    }

    public static void setCodeArea(CodeArea codeArea) {
        StaticResource.codeArea = codeArea;
    }

    public static UserFile getCurrentFile() {
        return currentFile;
    }

    public static void setCurrentFile(UserFile currentFile) {
        StaticResource.currentFile = currentFile;
    }

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        StaticResource.scene = scene;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        StaticResource.stage = stage;
    }

    public static ListView<UserFile> getCurrentFilesLV() {
        return currentFilesLV;
    }

    public static void setCurrentFilesLV(ListView<UserFile> currentFilesLV) {
        StaticResource.currentFilesLV = currentFilesLV;
    }

    public static ListView<UserFile> getClosedFilesLV() {
        return closedFilesLV;
    }

    public static void setClosedFilesLV(ListView<UserFile> closedFilesLV) {
        StaticResource.closedFilesLV = closedFilesLV;
    }

    public static int getCurrentFontSize() {
        return currentFontSize;
    }

    public static void setCurrentFontSize(int currentFontSize) {
        StaticResource.currentFontSize = currentFontSize;
    }

    public static Tab getCurrentFileTab() {
        return currentFileTab;
    }

    public static void setCurrentFileTab(Tab currentFileTab) {
        StaticResource.currentFileTab = currentFileTab;
    }
}
