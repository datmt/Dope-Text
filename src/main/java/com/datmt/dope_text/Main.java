package com.datmt.dope_text;

import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.manager.StaticResource;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import javax.swing.plaf.nimbus.State;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("ui.fxml")));
        primaryStage.setTitle("Dope Text");
        Scene scene = new Scene(root);
        registerHotKeys((scene));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void registerHotKeys(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            final KeyCombination save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            final KeyCombination createNew = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            final KeyCombination close = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (save.match(ke)) {

                    CodeArea codeArea = (CodeArea) scene.lookup("#codeArea");

                    if (StaticResource.currentFile != null) {
                        StaticResource.currentFile.setContent(codeArea.getText());
                        try {
                            DB db = new DB();
                            db.updateFile(StaticResource.currentFile.getId(), codeArea.getText());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                    }

                    ke.consume(); // <-- stops passing the event to next node
                } else if (createNew.match(ke)) {
                    System.out.println("Key Pressed: " + createNew);
                    ke.consume(); // <-- stops passing the event to next node
                }else if (close.match(ke)) {
                    System.out.println("Key Pressed: " + close);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
