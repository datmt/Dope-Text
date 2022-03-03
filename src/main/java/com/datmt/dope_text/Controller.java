package com.datmt.dope_text;

import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.File;
import com.datmt.dope_text.manager.StaticResource;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.sql.SQLException;
import java.util.List;

public class Controller {

    @FXML
    BorderPane rootPane;

    @FXML
    VBox startTab;

    @FXML
    ListView<File> currentFiles;

    List<File> allFiles;

    @FXML
    public void initialize() throws SQLException {
        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setContextMenu(new DefaultContextMenu());
        codeArea.setId("codeArea");
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        Label tutorial = new Label("Ctrl+S: Save, Ctrl+N: new, Ctrl+N: Close");

        startTab.getChildren().addAll(codeArea, tutorial);

        DB db = new DB();

        allFiles = db.getAllFiles();
        currentFiles.setItems(FXCollections.observableList(allFiles));

        StaticResource.codeArea = codeArea;

        currentFilesListViewEventHandler();

        loadLastOpenedFile();
    }


    private void updateCurrentlyOpenedFile(File file) throws SQLException {
        if (file == null || file.equals(StaticResource.currentFile)) {
            return;
        }

        CodeArea codeArea = StaticResource.codeArea;
        codeArea.replaceText(file.getContent());

        StaticResource.currentFile = file;

        DB db = new DB();
        db.updateLastOpenedFile(file.getId());
    }

    private void loadLastOpenedFile() throws SQLException {
        DB db = new DB();
        File f = null;
        f = db.getLastOpenedFile();
        if (f == null) {
            f = allFiles.size() > 0 ? allFiles.get(0) : null;
        }

        if (f!= null) {
            updateCurrentlyOpenedFile(f);
        }
    }


    private void currentFilesListViewEventHandler() {
        currentFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File f  = currentFiles.getSelectionModel().getSelectedItem();
                try {
                    updateCurrentlyOpenedFile(f);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private class DefaultContextMenu extends ContextMenu
    {
    }

    public void newFile() {

    }

}
