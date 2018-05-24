package bot;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import configuration.Config;
import dialogManager.DialogManager;
import knowledge.Reader;
import knowledge.SemanticNet;

public class ChatBot extends TelegramLongPollingBot {

	private final String error = "Ops... Riprova c'è stato un problema";
	private final String welcome = "Ciao, mantenere il bilancio familiare sempre aggiornato? ci penso io :)";
	private final String resetMessage = "Ho resettato il sistema.";
	private final String help = "cosa puoi fare?";
	private final String history = "storico";
	private final String add="ho avuto";
	private final String spent="ho speso";
		
	private String configPath;

	private DialogManager dm;

	public String getBotUsername() {
		return "ChatBot";
	}

	public ChatBot(String configPath) throws FileNotFoundException {
		this.configPath=configPath;
		resetDm();
	}

	private void resetDm() throws FileNotFoundException {
		Config conf = Config.getInstance(configPath);
		String url = conf.getPathSemanticNet();
		float threshold = 0.9f;
		SemanticNet net = new SemanticNet(url);
		JSONObject read = Reader.readNLU(net.getModel());
		dm = new DialogManager(net, threshold, read, conf);
		
	}

	public void onUpdateReceived(Update update) {
		// We check if the update has a message and the message has text
		if (update.hasMessage() && update.getMessage().hasText()) {
			String message_text = update.getMessage().getText();
			System.out.print("Messaggio ricevuto: " + message_text);
			Long chat_id = update.getMessage().getChatId();
			Integer user_id = update.getMessage().getFrom().getId();
			System.out.println(" da: " + user_id);
			if (message_text.equals("/start")) {
				sendMessage(chat_id, welcome);
			} else if(message_text.equals("/reset")){
				try {
					dm.setWorkingMemory(new JSONObject());
					dm.writeMemory();
					resetDm();
					sendMessage(chat_id, resetMessage);
					sendMessage(chat_id, welcome);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if(message_text.equals("/help")){
				synchronized (dm) {
					dm.setUser(user_id.toString());
					dm.streamMessage(help).stream().filter(s -> !s.isEmpty())
							.forEach(s -> sendMessage(chat_id, s));
				}
			} else if(message_text.equals("/history")){
				synchronized (dm) {
					dm.setUser(user_id.toString());
					dm.streamMessage(history).stream().filter(s -> !s.isEmpty())
							.forEach(s -> sendMessage(chat_id, s));
				}
			} else if(message_text.contains("/add")){
				message_text=message_text.replaceAll("/add", add);
				synchronized (dm) {
					dm.setUser(user_id.toString());
					dm.streamMessage(message_text).stream().filter(s -> !s.isEmpty())
							.forEach(s -> sendMessage(chat_id, s));
				}
			} else if(message_text.contains("/spent")){
				message_text=message_text.replaceAll("spent", spent);
				synchronized (dm) {
					dm.setUser(user_id.toString());
					dm.streamMessage(message_text).stream().filter(s -> !s.isEmpty())
							.forEach(s -> sendMessage(chat_id, s));
				}
			}else {
				synchronized (dm) {
					dm.setUser(user_id.toString());
					dm.streamMessage(message_text).stream().filter(s -> !s.isEmpty())
							.forEach(s -> sendMessage(chat_id, s));
				}
			}
		}
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
		return "366440532:AAFldkvvO-7rmZjuD4J1_SPcvXlcL5EN7cs";
	}
}
