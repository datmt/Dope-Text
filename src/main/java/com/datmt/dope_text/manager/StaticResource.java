package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.model.UserFile;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.fxmisc.richtext.CodeArea;

@Setter
@Getter
public class StaticResource {

    public static TextArea codeArea;
    public static UserFile currentFile;
    public static Scene scene;
    public static Stage stage;
    public static ListView<UserFile> currentFilesLV;
    public static ListView<UserFile> closedFilesLV;
    public static int currentFontSize = 13;
    public static Tab currentFileTab;
}
