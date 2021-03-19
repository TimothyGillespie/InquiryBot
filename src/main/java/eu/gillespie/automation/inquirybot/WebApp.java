package eu.gillespie.automation.inquirybot;

import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.config.Config;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.injector.Injector;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.SQL;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.LinkedList;

public class WebApp extends WebApplication {

    public static void main(String[] args) {
        WebApp instance = new WebApp();
        instance.run(args);
    }

    @Override
    protected void setupInjection(Injector injector) {
        super.setupInjection(injector);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Inquirer());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setupConfig(Config config) {
        config.addEnvFile(".env");
    }

    @Override
    protected void setupModels(SQL sql) throws ORMConfigurationException {

    }

    @Override
    protected void setupServer(HTTPServer httpServer) {

    }

    @Override
    protected void setupCommands(CommandSystem commandSystem) {

    }
}
