package com.datmt.dope_text.db.model;



public class UserFile {

    public UserFile(Long id, String fileHash, String localPath, String fileName, String content, Long createdTime, Long updatedTime, int isOpen) {
        this.id = id;
        this.fileHash = fileHash;
        this.localPath = localPath;
        this.fileName = fileName;
        this.content = content;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.isOpen = isOpen;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }
}
