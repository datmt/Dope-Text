package com.datmt.dope_text;

import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.File;
import com.datmt.dope_text.fx.FileListCell;
import com.datmt.dope_text.manager.CurrentFileManager;
import com.datmt.dope_text.manager.StaticResource;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    BorderPane rootPane;

    @FXML
    VBox startTab;

    @FXML
    TextField fileFilterTF;

    @FXML
    ListView<File> currentFiles;

    List<File> allCurrentlyOpenFiles;

    @FXML
    public void initialize() throws SQLException {
        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setContextMenu(new DefaultContextMenu());
        codeArea.setId("codeArea");
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        Label tutorial = new Label("Ctrl+S: Save, Ctrl+N: new, Ctrl+W: Close");

        startTab.getChildren().addAll(codeArea, tutorial);

        DB db = new DB();

        allCurrentlyOpenFiles = db.getAllOpenedFiles();
        currentFiles.setItems(FXCollections.observableList(allCurrentlyOpenFiles));
        currentFiles.setEditable(true);


        currentFiles.setCellFactory(param -> {
            FileListCell flc = new FileListCell();

            return flc;
        });

        StaticResource.codeArea = codeArea;

        currentFilesListViewEventHandler();

        loadLastOpenedFile();

        autosaveJob();
        registerFilterEvent();

    }

    private void registerFilterEvent() {
        fileFilterTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                currentFiles.setItems(FXCollections.observableList(allCurrentlyOpenFiles.stream().filter(t -> t.getFileName().toUpperCase(Locale.ROOT).contains(newValue.toUpperCase(Locale.ROOT))).collect(Collectors.toList())));
            }
        });
    }


    private void loadLastOpenedFile() throws SQLException {
        DB db = new DB();
        File f = null;
        f = db.getLastOpenedFile();
        if (f == null) {
            f = allCurrentlyOpenFiles.size() > 0 ? allCurrentlyOpenFiles.get(0) : null;
        }

        if (f != null) {
            File finalF = f;
            currentFiles.getSelectionModel().select(allCurrentlyOpenFiles.stream().filter(t -> t.getId().equals(finalF.getId())).findFirst().orElse(null));
            CurrentFileManager.updateCurrentlyOpenedFile(f);
        }


    }

    private void currentFilesListViewEventHandler() {

        currentFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File f = currentFiles.getSelectionModel().getSelectedItem();
                if (f == null) {
                    return;
                }

                if (f.equals(StaticResource.currentFile)) {
                    return;
                }

                try {
                    CurrentFileManager.saveFileBeforeSelectionChange(StaticResource.currentFile);
                    CurrentFileManager.updateCurrentlyOpenedFile(f);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private class DefaultContextMenu extends ContextMenu {
    }

    public void newFile() {

    }

    private void autosaveJob() {
        ScheduledService<Void> svc = new ScheduledService<>() {
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    protected Void call() {
                        // Connect to a Server
                        // Get the XML document
                        // Parse it into a document
                        if (!StaticResource.codeArea.getText().equals(StaticResource.currentFile.getContent())) {

                            try {
                                DB db = new DB();
                                StaticResource.currentFile.setContent(StaticResource.codeArea.getText());
                                db.updateFile(StaticResource.currentFile.getId(), StaticResource.codeArea.getText());
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }

                        }

                        return null;
                    }
                };
            }
        };
        svc.setPeriod(Duration.seconds(10));
        svc.start();
    }

}
