package bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {

    	ApiContextInitializer.init();

    	TelegramBotsApi botsApi = new TelegramBotsApi();

    	try {
    		System.out.println("INIT");
            botsApi.registerBot(new ChatBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
