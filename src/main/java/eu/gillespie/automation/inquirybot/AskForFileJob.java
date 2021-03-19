package eu.gillespie.automation.inquirybot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

import static eu.gillespie.automation.inquirybot.PropertyLoader.loadProperty;

public class AskForFileJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Inquirer inquirer = new Inquirer();

        SendMessage sendMessage = new SendMessage();
        assert inquirer.inquireeChatId != null;
        sendMessage.setChatId(inquirer.inquireeChatId);
        sendMessage.setText(Objects.requireNonNull(loadProperty("inquiry.ask-for-file-text")));
        try {
            inquirer.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
