package com.datmt.dope_text;

import com.datmt.dope_text.manager.HotkeyManager;
import com.datmt.dope_text.manager.StaticResource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("ui.fxml")));
        primaryStage.setTitle("Dope Text");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("style.css").toExternalForm()));
        registerHotKeys((scene));
        StaticResource.scene = scene;
        primaryStage.setScene(scene);
        primaryStage.show();
        StaticResource.stage = primaryStage;
    }

    private void registerHotKeys(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> HotkeyManager.manage(ke, scene));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
