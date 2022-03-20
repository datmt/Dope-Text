package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.model.UserFile;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.fxmisc.richtext.CodeArea;

import java.util.List;

@Setter
@Getter
public class StaticResource {
    public static CodeArea codeArea;
    public static UserFile currentFile;
    public static Scene scene;
    public static Stage stage;
    public static List<UserFile> allCurrentlyOpenFiles;
    public static ListView<UserFile> currentFilesLV;
    public static ListView<UserFile> closedFilesLV;
    public static int currentFontSize = 13;
}
