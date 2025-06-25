package com.uz;



import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YuklaBot extends TelegramLongPollingBot {
   InstagramDounload dounload=new InstagramDounload();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);


    @Override
    public String getBotUsername() {
        return ConfigLoader.get("bot.username");
    }


    @Override
    public String getBotToken() {
        return ConfigLoader.get("bot.token");
    }



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText().trim();

            if (text.equalsIgnoreCase("/start")) {
                sendText(chatId, "👋 Salom! YouTube yoki Instagram video URL sini yuboring.");
                return;
            }

            if (isSqlInjection(text)) {
                sendText(chatId, "🚫 Xabaringizda SQLga o‘xshash ifoda aniqlandi. Bu ruxsat etilmaydi.");
                return;
            }

            if (text.startsWith("http")) {
                if (isAllowedUrl(text)) {
                    executor.submit(new VideoDownloadTask(text, chatId, this));
                } else {
                    sendText(chatId, "❗ Faqat YouTube va Instagram havolalariga ruxsat beriladi.");
                }
            } else {
                sendText(chatId, "ℹ️ Iltimos, YouTube yoki Instagram video havolasini yuboring.");
            }
        }
    }

    private boolean isSqlInjection(String text) {
        String[] sqlKeywords = {"'", "--", ";", "/*", "*/", "or 1=1", "drop", "select", "insert", "update", "delete"};
        for (String keyword : sqlKeywords) {
            if (text.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedUrl(String text) {
        return text.contains("youtube.com") || text.contains("youtu.be") || text.contains("instagram.com");
    }



    public void sendText(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendVideo(long chatId, String videoPath) {
        try {
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                sendText(chatId, "Video fayli topilmadi.");
                return;
            }

            SendVideo video = new SendVideo();
            video.setChatId(chatId);
            video.setVideo(new InputFile(videoFile));
            execute(video);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendText(chatId, "Videoni yuborishda xatolik yuz berdi: " + e.getMessage());
        }
    }



    public void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("Faylni o'chirib bo'lmadi: " + filePath);
            }
        }
    }
}

