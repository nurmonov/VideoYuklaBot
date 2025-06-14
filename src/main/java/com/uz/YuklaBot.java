package com.uz;



import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

public class YuklaBot extends TelegramLongPollingBot {
   InstagramDounload dounload=new InstagramDounload();


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
            String text = update.getMessage().getText();

            if (text.equalsIgnoreCase("/start")) {
                sendText(chatId, "Salom! YouTube yoki Instagram video URL sini yuboring.");
            } else if (text.startsWith("http")) {
                sendText(chatId, "Video yuklanmoqda, iltimos kuting...");

                String videoFilePath;
                if (text.contains("instagram.com")) {
                    videoFilePath = dounload.downloadVideo(text, "insta");
                } else {
                    videoFilePath = dounload.downloadVideo(text, "video");
                }

                if (videoFilePath != null) {
                    sendVideo(chatId, videoFilePath);
                    deleteFile(videoFilePath);
                } else {
                    sendText(chatId, "Video yuklashda xatolik yuz berdi.");
                }
            } else {
                sendText(chatId, "Iltimos, video havolasini yuboring.");
            }
        }
    }

    private void sendText(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendVideo(long chatId, String videoPath) {
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



    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("Faylni o'chirib bo'lmadi: " + filePath);
            }
        }
    }
}

