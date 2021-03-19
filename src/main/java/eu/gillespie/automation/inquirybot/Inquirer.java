package eu.gillespie.automation.inquirybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.InputStream;

import static eu.gillespie.automation.inquirybot.PropertyLoader.loadProperty;

public class Inquirer extends TelegramLongPollingBot {

    final String inquireeChatId;
    final String inquirerChatId;


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

        if(update.hasMessage() && update.getMessage().hasDocument()) {
            InputStream document = getFileById(update.getMessage().getDocument().getFileId());

            if(document != null) {
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
    }

    private InputStream getFileById(String fileId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            var file = execute(getFile);
            return new TelegramFileDownloader(this::getBotToken).downloadFileAsStream(file);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
