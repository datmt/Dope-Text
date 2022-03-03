package com.datmt.dope_text.fx;


import com.datmt.dope_text.db.DB;
import com.datmt.dope_text.db.model.File;
import com.datmt.dope_text.manager.StaticResource;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

import java.sql.SQLException;

public class FileListCell extends TextFieldListCell<File> {

    public FileListCell() {
        super();
        updateFileName();
    }

    private void updateFileName() {
        StringConverter<File> converter = new StringConverter<>() {
            @Override
            public String toString(File file) {
                return file.getFileName();
            }

            @Override
            public File fromString(String string) {
                if (isEmpty()) {
                    return null;
                }
                File file = getItem();
                file.setFileName(string);
                try {
                    StaticResource.stage.setTitle(file.getFileName() + " | Dope Text");
                    DB db = new DB();
                    db.updateFileName(file.getId(), string);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }


                return file;
            }
        };
        setConverter(converter);
    }

    @Override
    public void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        updateFileName();
    }
}
