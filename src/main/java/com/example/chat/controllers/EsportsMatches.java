package com.example.chat.controllers;

import com.toornament.ToornamentClient;
import com.toornament.model.Discipline;
import com.toornament.model.Tournament;
import com.toornament.model.enums.ScheduledSort;
import com.toornament.model.request.TournamentQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
public class EsportsMatches {
	
	ToornamentClient client;
	
	@Autowired
	public EsportsMatches(ToornamentClient toornamentClient) {
		this.client = toornamentClient;
	}
	
	@GetMapping("/games")
	public String games() {
		List<Discipline> disciplines = new ArrayList<>(
				this.client.disciplines().getDisciplines("disciplines=0-9"));
		StringBuffer discList = new StringBuffer();
		disciplines.forEach(a ->
				discList.append(a.getName() + ": " + a.getId()+" ")
		);
		
		return discList.toString();
	}
	
	@GetMapping("/tournaments/{game}")
	public String tournaments(@PathVariable("game") String game){
		StringBuffer buffer = new StringBuffer();
		TournamentQuery.TournamentQueryBuilder query = TournamentQuery.builder();
		//gets up to ten featured tournaments happening this month
		query.discipline(game).sort(ScheduledSort.SCHEDULED_DESC).isOnline(false)
				.scheduledBefore(
						LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
								LocalDate.now().getMonth().length(false)));
		
		HashMap<String, String> map = new HashMap<>(1);
		map.put("range", "tournaments=0-9");
		List<Tournament> tournaments;
		tournaments = this.client.tournaments()
				.getFeaturedTournaments(query.build(), map);
		
		if(tournaments.isEmpty())
			return "No featured tournaments found for this month";
		
		tournaments.forEach(a -> buffer.append(a.getName()).append(", "));
		return buffer.toString();
	}
}
