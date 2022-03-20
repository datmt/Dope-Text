package com.datmt.dope_text.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

public class FileHelper {
    private static Logger logger = LogManager.getLogger(FileHelper.class.getName());
    public static void saveToDisk(String filePath, String content) {
        logger.info("Saving file to disk {}", filePath);
        try {
            PrintWriter writer;
            writer = new PrintWriter(filePath);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
