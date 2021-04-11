package com.example.chat.views.chat;

import com.github.appreciated.card.Card;
import com.github.appreciated.card.content.IconItem;
import com.toornament.ToornamentClient;
import com.toornament.model.Tournament;
import com.toornament.model.enums.Scope;
import com.toornament.model.request.TournamentQuery;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import java.io.IOException;

import java.util.HashSet;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.Avataaar;
@Service
public class MessageList extends Div {
	
	Logger logger;
	ToornamentClient client;
	String currentDiscipline;
	
	@Autowired
	public MessageList(Logger logger) {
		this.logger = logger;
		HashSet<Scope> scopes = new HashSet<>();
		scopes.add(Scope.ORGANIZER_VIEW);
		
		this.client = new ToornamentClient("yEF4GKOHO6MDYWh4q_6u0mHO5KfEVu1gAN20Dr76GtI",
				"58ff4401140ba08e7f8b4567269ltppwn480ggw08gc4ggkcccwsgsog4cssc80c8swgkwg0so",
				"nyfrwu8nsfko8cwckgwco840csc0k4wcog0gw84gwo440gggg", scopes);
	}
	
	public void addMessage(String from, Avataaar avatar, String text, boolean isCurrentUser) {
		Span fromContainer = new Span(new Text(from));
		fromContainer.addClassName(getClass().getSimpleName() + "-name");
		
		Div textContainer = new Div(new Text(text));
		textContainer.addClassName(getClass().getSimpleName() + "-bubble");
		
		Div avatarContainer = new Div(avatar, fromContainer);
		avatarContainer.addClassName(getClass().getSimpleName() + "-avatar");
		
		Div line = new Div(avatarContainer, textContainer);
		line.addClassName(getClass().getSimpleName() + "-row");
		add(line);
		
		if (isCurrentUser) {
			line.addClassName(getClass().getSimpleName() + "-row-currentUser");
			textContainer.addClassName(getClass().getSimpleName() + "-bubble-currentUser");
		}
		
		line.getElement().callJsFunction("scrollIntoView");
	}
	
	public void esportsMessage(String from, Avataaar avatar, String ans, boolean isCurrentUser,
			String imagePath, String title, String description) {
		Card card;
		Image img;
		
		img = new Image(imagePath, title);
		img.setWidth("256px");
		img.setHeight("256px");
		card = new Card(
				new IconItem(img, title, description).withWhiteSpaceNoWrap()
		);
		card.setWidth("100%");
		
		Span fromContainer = new Span(new Text(from));
		fromContainer.addClassName(getClass().getSimpleName() + "-name");
		
		Card chatcard = new Card(card);
		chatcard.addClassName(getClass().getSimpleName());
		
		Div textContainer = new Div(new Text(ans), new Text(question(ans)));
		textContainer.addClassName(this.getClass().getSimpleName() + "-bubble");
		
		Div avatarContainer = new Div(avatar, fromContainer);
		avatarContainer.addClassName(getClass().getSimpleName() + "-avatar");
		
		Div line = new Div(avatarContainer, textContainer);
		line.addClassName(getClass().getSimpleName() + "-row");
		add(line);
		
		if (isCurrentUser) {
			line.addClassName(getClass().getSimpleName() + "-row-currentUser");
			textContainer.addClassName(getClass().getSimpleName() + "-bubble-currentUser");
		}
		
		line.getElement().callJsFunction("scrollIntoView");
	}
	
	private String question(String ans) {
		String qAnswer = "";
		//just to ignore answer listing options
		if (ans.contains("Here are some tournaments for")) {
			if (ans.toLowerCase(Locale.ROOT).contains("overwatch")) {
				qAnswer = getFeaturedTournaments("overwatch");
				this.currentDiscipline = "overwatch";
			}
			if (ans.toLowerCase(Locale.ROOT).contains("league of legends") || ans.contains("LoL")) {
				qAnswer = getFeaturedTournaments("leagueoflegends");
				this.currentDiscipline = "leagueoflegends";
			}
			if (ans.toLowerCase(Locale.ROOT).contains("fortnite")) {
				qAnswer = getFeaturedTournaments("fortnite");
				this.currentDiscipline = "fortnite";
			}
			
		}
		String search = ans.substring(24);
		logger.debug(ans);
		if (ans.contains("Here are the matches for")) {
			logger.debug("group 1: {}",search.trim());
			qAnswer = getMatches(search.trim().substring(0,search.trim().length()-1));
			
		}
		
		return qAnswer;
	}
	
	private String getMatches(String group) {
		OkHttpClient client = new OkHttpClient();
		Request.Builder builder = new Request.Builder().url("http://localhost:8080/matches/list/".concat(currentDiscipline).concat("/").concat(group));
		String ans;
		try {
			ans = client.newCall(builder.build()).execute().body().string();
		} catch (IOException e) {
			ans = "something went wrong with the call";
			logger.error(e.getMessage());
		}
		return ans;
	}
	
	private String getFeaturedTournaments(String game) {
		OkHttpClient client = new OkHttpClient();
		Request.Builder builder = new Request.Builder().url("http://localhost:8080/matches/tournaments/".concat(game));
		String ans;
		try {
			ans = client.newCall(builder.build()).execute().body().string();
		} catch (IOException e) {
			ans = "something went wrong with the call";
			logger.error(e.getMessage());
		}
		return ans;
		
	}
	
}
