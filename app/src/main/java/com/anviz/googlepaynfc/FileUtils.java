package com.anviz.googlepaynfc;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static void writeFile(File file, String content) {
        if (file.isDirectory()) {
            return;
        }
        try(FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(content);
            bw.flush();
            osw.flush();
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) {
        final File file = new File(path);
        if (file.isDirectory()) {
            return "";
        }
        if (!file.exists()) {
            return "";
        }
        String result = "";
        try {
            result = readFile(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String readFile(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
