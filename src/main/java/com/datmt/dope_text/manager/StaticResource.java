package com.datmt.dope_text.manager;

import com.datmt.dope_text.db.model.File;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.fxmisc.richtext.CodeArea;

@Setter
@Getter
public class StaticResource {
    public static CodeArea codeArea;
    public static File currentFile;
    public static Scene scene;
    public static Stage stage;
}
