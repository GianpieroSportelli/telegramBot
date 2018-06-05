package bot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


public class BrokerBot extends TelegramLongPollingBot {
	private final String help = "hai bisogno di aiuto? chiedi a @GianpSport";
	private final String error = "Ops... Riprova c'è stato un problema nel salvataggio della conversazione chiedi a @GianpSport";
	private final String welcome = "Ciao, sono un broker bot, metto in contatto due utenti per costruire gli script delle conversazioni.";
	
	private final Set<Long> users= new HashSet<>();
	private final Set<Long> bots= new HashSet<>();
	private final BiMap<Long,Long> map= HashBiMap.create() ;
	private final Map<Long,String> nameChat = new HashMap<>();
		
	private String configPath;

	public String getBotUsername() {
		return "ozBrokerBot";
	}

	public BrokerBot(String configPath) {
		this.configPath=configPath;
	}

	

	public void onUpdateReceived(Update update) {
		// We check if the update has a message and the message has text
		if (update.hasMessage() && update.getMessage().hasText()) {
			String message_text = update.getMessage().getText();
			System.out.print("Messaggio ricevuto: " + message_text);
			Long chat_id = update.getMessage().getChatId();
			Integer user_id = update.getMessage().getFrom().getId();
			String name = update.getMessage().getFrom().getFirstName();
			
			if(!nameChat.containsKey(chat_id)) {
				nameChat.put(chat_id, name+user_id);
			}
			
			System.out.println(" da: " +name+"-"+ user_id);
			if (message_text.equals("/start")) {
				sendMessage(chat_id, welcome);
			}else if (message_text.equals("/help")) {
				sendMessage(chat_id, help);
			}else if(message_text.equals("/join")) {
				String message;
				if(users.size()>0) {
					Long user = users.iterator().next();
					users.remove(user);
					map.put(user, chat_id);
					message="sei stato registrato come <b>bot</b>. Il tuo <b>user</b> è già disponibile preparati a rispondere :)";
				}else if(bots.size()>0) {
					Long bot = bots.iterator().next();
					bots.remove(bot);
					map.put(chat_id,bot);
					message="sei stato registrato come <b>user</b>. Il tuo <b>bot</b> è già disponibile puoi scrivere il primo messaggio";
				}else {
					Random r=new Random();
					if(r.nextBoolean()) {
						users.add(chat_id);
						message="sei stato registrato come <b>user</b>. Dovrai simulare un utente non appena un <b>bot</b> sarà disponibile";
					}else {
						bots.add(chat_id);
						message="sei stato registrato come <b>bot</b>. Dovrai simulare l'agente conversazionale non appena uno <b>user</b> sarà disponibile";
					}
				}
				sendMessage(chat_id, message);
			}else {
				if(map.containsKey(chat_id)) {
					Long bot=map.get(chat_id);
					sendMessage(bot, message_text);
					try {
						writeMessage(chat_id, bot, "USER >> "+message_text);
					} catch (IOException e) {
						sendMessage(chat_id,error);
					} 
				}else if(map.containsValue(chat_id)) {
					Long user=map.inverse().get(chat_id);
					sendMessage(user, message_text);
					try {
						writeMessage(user,chat_id, "BOT  >> "+message_text);
					} catch (IOException e) {
						sendMessage(chat_id, error);
					} 
				}else {
					if(users.contains(chat_id)) {
						sendMessage(chat_id, "sei ancora in attesa di un bot");
					}else if(bots.contains(chat_id)) {
						sendMessage(chat_id, "sei ancora in attesa di uno user");
					}else {
						sendMessage(chat_id, "devi ancora registrarti");
					}
				}
			}
		}
	}
	
	private void writeMessage(Long userChatId,Long botChatId, String message) throws IOException {
		String nameUser= nameChat.get(userChatId);
		String nameBot = nameChat.get(botChatId);
		String fileName= nameUser+"_"+nameBot+".txt";
		String path = ((configPath.charAt(configPath.length()-1)=='/'|| configPath.charAt(configPath.length()-1)=='\\')?configPath:configPath+"/")+fileName;
		java.io.File report = new java.io.File(path);
		if(!report.exists()) {
			report.createNewFile();
		}
		
		FileWriter out=new FileWriter(path, true);
		out.write(message+"\n");
		
		out.close();
		
	}

	private void sendMessage(Long chat_id, String msg) {
		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chat_id).setParseMode("HTML").setText(msg);

		try {
			execute(message); // Call method to send the message
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return "616850343:AAE0kfG2JDmE8qSlZks0Yc-NApBCjiZl8ds";
	}
}
