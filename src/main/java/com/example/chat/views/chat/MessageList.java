package com.example.chat.views.chat;

import ch.qos.logback.classic.Logger;
import com.github.appreciated.card.Card;
import com.github.appreciated.card.content.IconItem;
import com.toornament.ToornamentClient;
import com.toornament.concepts.Matches;
import com.toornament.model.Discipline;
import com.toornament.model.Match;
import com.toornament.model.MatchDetails;
import com.toornament.model.Tournament;
import com.toornament.model.TournamentDetails;
import com.toornament.model.enums.ScheduledSort;
import com.toornament.model.enums.Scope;
import com.toornament.model.enums.TournamentStatus;
import com.toornament.model.request.MatchQuery;
import com.toornament.model.request.TournamentQuery;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import org.apache.http.client.HttpClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.artur.Avataaar;

public class MessageList extends Div {
	Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	ToornamentClient client;
	String currentDiscipline;
	final String regex = "(\\w+ \\w+)";
	final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	
	public MessageList() {
		
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
			if (ans.toLowerCase(Locale.ROOT).contains("valorant")) {
				qAnswer = getFeaturedTournaments("valorant");
				this.currentDiscipline = "valorant";
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
		Logger logger = (Logger) LoggerFactory.getLogger("question");
		logger.debug(ans);
		if (!ans.contains("Here are some tournaments for")) {
			logger.debug("group 1: {}",search.trim());
			qAnswer = getMatches(search.trim().substring(0,search.trim().length()-1), ans);
			
		}
		
		return qAnswer;
	}
	
	private String getMatches(String group, String ans) {
		OkHttpClient client = new OkHttpClient();
		Request.Builder builder = new Request.Builder().url("http://localhost:8080/matches/list/".concat(currentDiscipline).concat("/").concat(group));
		
		try {
			ans = client.newCall(builder.build()).execute().body().string();
		} catch (IOException e) {
			ans = "something went wrong with the call";
			logger.error(e.getMessage());
		}
		return ans;
	}
	
	private String getFeaturedTournaments(String game) {
		StringBuffer buffer = new StringBuffer();
		TournamentQuery.TournamentQueryBuilder query = TournamentQuery.builder();
		//gets up to ten featured tournaments
		query.discipline(game)
				.scheduledBefore(
						LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
								LocalDate.now().getMonth().length(false)));
		
		HashMap<String, String> map = new HashMap<>(1);
		map.put("range", "tournaments=0-9");
		
		List<Tournament> tournaments = client.tournaments()
				.getFeaturedTournaments(query.build(), map);
		
		if (tournaments.isEmpty()) {
			return "No featured tournaments found";
		}
		
		tournaments.stream().forEach(a -> buffer.append(a.getName()).append(", "));
		buffer.replace(buffer.length() - 2, buffer.length(), ".");
		return buffer.toString();
		
	}
	
}
