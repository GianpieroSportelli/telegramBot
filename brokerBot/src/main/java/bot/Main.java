package bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args){
    	
    	String configPath="./";
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
            botsApi.registerBot(new BrokerBot(configPath));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
