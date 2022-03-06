package com.datmt.dope_text.manager;


import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.File;
import org.fxmisc.richtext.CodeArea;

import java.sql.SQLException;

public class CurrentFileManager {
    public static void updateCurrentlyOpenedFile(File file) throws SQLException {
        if (file == null || file.equals(StaticResource.currentFile)) {
            return;
        }

        CodeArea codeArea = StaticResource.codeArea;
        codeArea.replaceText(file.getContent());
        codeArea.scrollToPixel(0, 0);
        StaticResource.currentFile = file;

        DB db = new DB();
        db.updateLastOpenedFile(file.getId());

        if (StaticResource.stage != null) {
            StaticResource.stage.setTitle(file.getFileName() + " | Dope Text");
        }


    }

    public static void saveFileBeforeSelectionChange(File file) throws SQLException {

        if (file.getContent().equals(StaticResource.codeArea.getText()))
            return;

        DB db = new DB();
        file.setContent(StaticResource.codeArea.getText());
        db.updateFile(file.getId(), StaticResource.codeArea.getText());
    }

    public static void exportToLocalFile() {

    }

    public static void openLocalFile() {

    }
}
