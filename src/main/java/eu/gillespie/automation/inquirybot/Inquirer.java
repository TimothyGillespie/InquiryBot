package eu.gillespie.automation.inquirybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

import static eu.gillespie.automation.inquirybot.PropertyLoader.loadProperty;

public class Inquirer extends TelegramLongPollingBot {

    public final String inquireeChatId;
    public final String inquirerChatId;


    Inquirer() {
        this.inquireeChatId = loadProperty("inquiry.inquiree-chat-id");
        this.inquirerChatId = loadProperty("inquiry.inquirer-chat-id");
    }

    @Override
    public String getBotUsername() {
        return loadProperty("telegram.bot-name");
    }

    @Override
    public String getBotToken() {
        return loadProperty("telegram.api-key");
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.getMessage().getChatId().toString().equals(this.inquireeChatId) && update.hasMessage()) {

            if (update.getMessage().hasDocument()) {
                InputStream document = getFileById(update.getMessage().getDocument().getFileId());

                if (document != null) {
                    InputFile inputFile = new InputFile();
                    inputFile.setMedia(document, "providedFileFrom" + update.getMessage().getFrom().getFirstName() + update.getMessage().getFrom().getLastName());

                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(this.inquirerChatId);
                    sendDocument.setDocument(inputFile);

                    try {
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        SendMessage errorMessage = new SendMessage();
                        errorMessage.setChatId(this.inquirerChatId);
                        errorMessage.setText("An error occured.");
                    }
                }
            }

            if (update.getMessage().hasText()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(this.inquirerChatId);
                sendMessage.setText("Von " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName() + ": " + update.getMessage().getText());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private InputStream getFileById(String fileId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            File file = execute(getFile);
            return new TelegramFileDownloader(this::getBotToken).downloadFileAsStream(file);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
