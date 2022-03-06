package com.datmt.dope_text.helper;

import java.io.IOException;
import java.io.PrintWriter;

public class FileHelper {
    public static void saveToDisk(String filePath, String content) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(filePath);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            Log1.logger.error(ex);
        }
    }
}
