package bot;

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

	private String error = "Ops... Riprova c'è stato un problema";
	private String welcome = "Ciao, mantenere il bilancio familiare sempre aggiornato? ci penso io :)";

	private final DialogManager dm;

	public String getBotUsername() {
		return "ChatBot";
	}

	public ChatBot() {
		String url = Config.getPathSemanticNet();
		float threshold = 0.7f;
		SemanticNet net = new SemanticNet(url);
		JSONObject read = Reader.readNLU(net.getModel());
		Config conf = Config.getInstance();
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
			} else {
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
