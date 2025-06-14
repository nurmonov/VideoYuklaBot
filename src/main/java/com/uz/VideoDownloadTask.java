package com.uz;



public class VideoDownloadTask implements Runnable {
    private final String url;
    private final long chatId;
    private final YuklaBot bot;

    public VideoDownloadTask(String url, long chatId, YuklaBot bot) {
        this.url = url;
        this.chatId = chatId;
        this.bot = bot;
    }

    @Override
    public void run() {
        bot.sendText(chatId, "📥 Video yuklanmoqda, iltimos kuting...");
        String filePath = null;
        if (url.contains("instagram.com")) {
            filePath = new InstagramDounload().downloadVideo(url, "insta", chatId);
        } else {
            filePath = new InstagramDounload().downloadVideo(url, "video",chatId);
        }

        if (filePath != null) {
            bot.sendVideo(chatId, filePath);
            bot.deleteFile(filePath);
        } else {
            bot.sendText(chatId, "❌ Video yuklashda xatolik yuz berdi.");
        }
    }
}

