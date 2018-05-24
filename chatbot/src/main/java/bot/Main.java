package bot;

import java.io.FileNotFoundException;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
    	
    	String configPath="./ConversationalAgent.properties";
    	if(args.length==0) {
    		System.out.println("use default config path: "+configPath);
    	}else {
    		configPath=args[0];
    		System.out.println("use path path: "+configPath);
    	}

    	ApiContextInitializer.init();

    	TelegramBotsApi botsApi = new TelegramBotsApi();

    	try {
    		System.out.println("INIT");
            botsApi.registerBot(new ChatBot(configPath));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
