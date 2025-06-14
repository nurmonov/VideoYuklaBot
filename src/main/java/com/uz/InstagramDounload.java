package com.uz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InstagramDounload {

    public String downloadVideo(String url, String prefix,long chatId) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outputFilePath = ConfigLoader.get("download.folder")  + prefix + "_chat" + chatId + "_" + timestamp + ".mp4";

        ProcessBuilder builder = new ProcessBuilder(
                ConfigLoader.get("yt.dlp.path"),
                "-f", "mp4",
                "-o", outputFilePath,
                url
        );

        builder.redirectErrorStream(true);

        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return outputFilePath;
            } else {
                System.err.println("yt-dlp xatolik bilan tugadi: " + exitCode);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
