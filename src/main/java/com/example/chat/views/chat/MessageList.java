package com.example.chat.views.chat;

import com.github.appreciated.card.Card;
import com.github.appreciated.card.content.IconItem;
import com.toornament.ToornamentClient;
import com.toornament.model.Discipline;
import com.toornament.model.Tournament;
import com.toornament.model.enums.ScheduledSort;
import com.toornament.model.enums.Scope;
import com.toornament.model.enums.TournamentStatus;
import com.toornament.model.request.TournamentQuery;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.artur.Avataaar;

public class MessageList extends Div {
	
	ToornamentClient client;
	
	public MessageList() {
		
		HashSet<Scope> scopes = new HashSet<>();
		scopes.add(Scope.ORGANIZER_VIEW);
		
		this.client = new ToornamentClient("yEF4GKOHO6MDYWh4q_6u0mHO5KfEVu1gAN20Dr76GtI",
				"58ff4401140ba08e7f8b4567269ltppwn480ggw08gc4ggkcccwsgsog4cssc80c8swgkwg0so",
				"nyfrwu8nsfko8cwckgwco840csc0k4wcog0gw84gwo440gggg", scopes);
		client.authorize();
		
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
		StringBuffer buffer = new StringBuffer();
		//just to ignore answer listing options
		if(ans.contains("Here are some tournaments for")) {
			if (ans.toLowerCase(Locale.ROOT).contains("overwatch")) {
				TournamentQuery.TournamentQueryBuilder query = TournamentQuery.builder();
				//gets up to ten featured tournaments happening this month
				query.discipline("overwatch").sort(ScheduledSort.SCHEDULED_DESC).status(
						TournamentStatus.RUNNING).scheduledAfter(
						LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1))
						.scheduledBefore(
								LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
										LocalDate.now().getMonth().length(false)));
				
				HashMap<String, String> map = new HashMap<>(1);
				map.put("range", "tournaments=0-9");
				
				List<Tournament> tournaments = client.tournaments()
						.getFeaturedTournaments(query.build(), map);
				
				if(tournaments.isEmpty())
					return "No featured tournaments found for this month";
				
				tournaments.stream().forEach(a -> buffer.append(a.getName()).append("/n"));
				
			}
			
		}
		
		return buffer.toString();
	}
	
}
