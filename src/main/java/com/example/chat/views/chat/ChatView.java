package com.example.chat.views.chat;

import com.example.chat.views.main.MainView;
import com.github.appreciated.card.Card;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.scopes.VaadinUIScope;
import org.goldrenard.jb.core.Bot;
import org.goldrenard.jb.core.Chat;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.artur.Avataaar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Route(value = "chat", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Chat")
@CssImport("styles/views/chat/chat-view.css")
@Component
@Scope(VaadinUIScope.VAADIN_UI_SCOPE_NAME)
public class ChatView extends VerticalLayout {
	
	private final UI ui;
	private final MessageList messageList = new MessageList();
	private final EsportsCard esportsCard = new EsportsCard();
	private final TextField message = new TextField();
	private final Chat chatSession;
	private final ScheduledExecutorService executorService;
	
	public ChatView(Bot alice, ScheduledExecutorService executorService) {
		this.executorService = executorService;
		ui = UI.getCurrent();
		chatSession = new Chat(alice);
		
		message.setPlaceholder("Enter a message...");
		message.setSizeFull();
		
		Button send = new Button(VaadinIcon.ENTER.create(), event -> sendMessage());
		send.addClickShortcut(Key.ENTER);
		
		HorizontalLayout inputLayout = new HorizontalLayout(message, send);
		inputLayout.addClassName("inputLayout");
		
		add(messageList, inputLayout);
		expand(messageList);
		setSizeFull();
	}
	
	private void sendMessage() {
		String text = message.getValue();
		messageList.addMessage("You", new Avataaar("Name"), text, true);
		message.clear();
		
		executorService.schedule(() -> {
			String answer = chatSession.multisentenceRespond(text);
			ui.access(() -> {messageList.esportsMessage("Alice", new Avataaar("Alice2"), answer, false,"https://www.toornament.com/media/file/2220518532853956608/logo_large?v=1549610945", "Interesting.",
					"Logo");
			});
		}, 100, TimeUnit.MILLISECONDS);
		
//		executorService.schedule(()->{
//			String answer = chatSession.multisentenceRespond(text);
//			ui.access(() -> esportsCard.addMessage("Alice", new Avataaar("Alice2"),answer,false));
//			},100, TimeUnit.MILLISECONDS);
	}
	
}
