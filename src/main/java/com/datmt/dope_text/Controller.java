package com.datmt.dope_text;

import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.UserFile;
import com.datmt.dope_text.fx.FileListCell;
import com.datmt.dope_text.helper.FileHelper;
import com.datmt.dope_text.helper.TextSearcher;
import com.datmt.dope_text.helper.UserPrefs;
import com.datmt.dope_text.manager.CurrentFileManager;
import com.datmt.dope_text.manager.StaticResource;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
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
    TextField closedFileFilterTF;

    @FXML
    ListView<UserFile> currentFilesLV;

    @FXML
    ListView<UserFile> closedFilesLV;

    List<UserFile> allCurrentlyOpenFiles;

    List<UserFile> allClosedFiles;

    @FXML
    TextField searchTF;

    @FXML
    CheckBox wrapTexCheckbox;

    @FXML
    TabPane leftSideTabPane;

    @FXML
    Label dbLocationLB;

    @FXML
    Tab currentFileTab;

    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    @FXML
    public void initialize() throws SQLException {
        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setContextMenu(new DefaultContextMenu());
        codeArea.setId("codeArea");
        startTab.getChildren().addAll(codeArea);

        DB db = new DB();

        allCurrentlyOpenFiles = db.getAllOpenedFiles();
        allClosedFiles = db.getAllClosedFiles();
        currentFilesLV.setItems(FXCollections.observableList(allCurrentlyOpenFiles));
        currentFilesLV.setEditable(true);

        closedFilesLV.setItems(FXCollections.observableList(allClosedFiles));


        currentFilesLV.setCellFactory(param -> {
            FileListCell flc = new FileListCell();

            return flc;
        });

        codeArea.setAutoHeight(true);
        VBox.setVgrow(codeArea, Priority.ALWAYS);

        StaticResource.codeArea = codeArea;
        StaticResource.currentFilesLV = currentFilesLV;
        StaticResource.closedFilesLV = closedFilesLV;
        StaticResource.currentFileTab = currentFileTab;

        currentFilesListViewEventHandler();
        closeFilesListViewEventHandler();

        loadLastOpenedFile();

        autosaveJob();
        registerFilterEvent();

        populateDBLocation();
        logger.info("start version 1.0.6");
    }

    private void populateDBLocation() {
        dbLocationLB.setWrapText(true);
        if (UserPrefs.getDbLocation() != null) {
            dbLocationLB.setText(UserPrefs.getDbLocation());
        }
    }

    private void registerFilterEvent() {
        fileFilterTF.textProperty().addListener((observable, oldValue, newValue) -> currentFilesLV.setItems(FXCollections.observableList(allCurrentlyOpenFiles.stream().filter(t -> t.getFileName().toUpperCase(Locale.ROOT).contains(newValue.toUpperCase(Locale.ROOT))).collect(Collectors.toList()))));
        closedFileFilterTF.textProperty().addListener((observable, oldValue, newValue) -> closedFilesLV.setItems(FXCollections.observableList(allClosedFiles.stream().filter(t -> t.getFileName().toUpperCase(Locale.ROOT).contains(newValue.toUpperCase(Locale.ROOT))).collect(Collectors.toList()))));
    }


    private void loadLastOpenedFile() throws SQLException {
        DB db = new DB();
        UserFile f = null;
        f = db.getLastOpenedFile();
        if (f == null) {
            f = allCurrentlyOpenFiles.size() > 0 ? allCurrentlyOpenFiles.get(0) : null;
        }

        if (f != null) {
            UserFile finalF = f;
            currentFilesLV.getSelectionModel().select(allCurrentlyOpenFiles.stream().filter(t -> t.getId().equals(finalF.getId())).findFirst().orElse(null));
            CurrentFileManager.updateCurrentlyOpenedFile(f);
            CurrentFileManager.selectCurrentFileById(f.getId(), currentFilesLV);
            currentFileTab.setText(f.getFileName());
        }


    }

    private void currentFilesListViewEventHandler() {

        currentFilesLV.setOnMouseClicked(event -> {
            UserFile f = currentFilesLV.getSelectionModel().getSelectedItem();
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
        });

    }


    private void closeFilesListViewEventHandler() {

        closedFilesLV.setOnMouseClicked(event -> {

            if (event.getClickCount() < 2) {
                return;
            }

            UserFile f = closedFilesLV.getSelectionModel().getSelectedItem();
            if (f == null) {
                return;
            }

            try {
                DB db = new DB();
                db.updateFileOpenStatus(f.getId(), 1);
                //add this file to the current file list
                currentFilesLV.getItems().add(f);
                closedFilesLV.getItems().remove(f);
                CurrentFileManager.saveFileBeforeSelectionChange(StaticResource.currentFile);
                CurrentFileManager.updateCurrentlyOpenedFile(f);
                currentFilesLV.getSelectionModel().select(f);
                leftSideTabPane.getSelectionModel().select(0);//switch to the first tab
            } catch (SQLException e) {
                e.printStackTrace();
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
                                StaticResource.currentFile.setContent(StaticResource.codeArea.getText());
                                CurrentFileManager.saveCurrentFileToDB(StaticResource.codeArea.getText(), StaticResource.currentFile.getId());
                                CurrentFileManager.saveCurrentFileToDisk();
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


    public void searchText() {
        if (searchTF.getText() != null && !searchTF.getText().equals(""))
            TextSearcher.highlightMatchText(StaticResource.codeArea.getText(), searchTF.getText());
        else
            TextSearcher.clearHighlight();
    }

    public void toggleWrap() {
        StaticResource.codeArea.setWrapText(wrapTexCheckbox.isSelected());
    }

    public void changeDbLocation() {
        FileChooser chooser = new FileChooser();
        File f = chooser.showOpenDialog(rootPane.getScene().getWindow());

        if (f != null) {
            UserPrefs.setDbLocation(f.getAbsolutePath());
            dbLocationLB.setText(f.getAbsolutePath());
        }
    }
}
