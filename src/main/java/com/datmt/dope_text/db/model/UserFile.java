package com.datmt.dope_text.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserFile {

    Long id;
    String fileHash;
    String localPath;
    String fileName;
    String content;
    Long createdTime;
    Long updatedTime;
    int isOpen;

    @Override
    public String toString() {
        return fileName;
    }
}
