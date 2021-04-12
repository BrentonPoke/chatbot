package com.example.chat.controllers;


import com.toornament.ToornamentClient;
import com.toornament.concepts.Matches;
import com.toornament.model.Discipline;
import com.toornament.model.MatchDetails;
import com.toornament.model.Opponent;
import com.toornament.model.Tournament;
import com.toornament.model.TournamentDetails;
import com.toornament.model.enums.MatchStatus;
import com.toornament.model.enums.MatchType;
import com.toornament.model.enums.Result;
import com.toornament.model.header.DisciplinesHeader;
import com.toornament.model.header.MatchesHeader;
import com.toornament.model.header.TournamentsHeader;
import com.toornament.model.request.MatchQuery;
import com.toornament.model.request.TournamentQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
public class EsportsMatches {
	
	ToornamentClient client;
	Logger logger;
	@Autowired
	public EsportsMatches(ToornamentClient toornamentClient, Logger logger) {
		this.logger = logger;
		this.client = toornamentClient;
	}
	
	@GetMapping("/games")
	public String games() {
		DisciplinesHeader header = new DisciplinesHeader().build(0,9);
		List<Discipline> disciplines = new ArrayList<>(
				this.client.disciplines().getDisciplines(header));
		StringBuffer discList = new StringBuffer();
		disciplines.forEach(a ->
				discList.append(a.getName() + ": " + a.getId()+" ")
		);
		
		return discList.toString();
	}
	
	@GetMapping("/list/{discipline}/{search}")
	public String getMatches(@PathVariable("discipline")String discipline, @PathVariable("search") String search){
		StringBuffer buffer = new StringBuffer();
		TournamentQuery.TournamentQueryBuilder query = TournamentQuery.builder();
		
		query.discipline(discipline)
				.name(search);
		
		logger.debug(query.toString());
		TournamentsHeader header = new TournamentsHeader().build(0,9);
		
		List<Tournament> tournaments;
		tournaments = this.client.tournaments().getFeaturedTournaments(query.build(),header);
		
		logger.debug(tournaments.toString());
		
		Stream<Tournament> stream = tournaments.stream().filter(t -> t.getName().toLowerCase(Locale.ROOT).contains(search.toLowerCase(
				Locale.ROOT)));
		tournaments = stream.collect(Collectors.<Tournament>toList());
		
		logger.debug("After filter: {}", tournaments);
		
		TournamentDetails details = new TournamentDetails();
		details.setDiscipline(discipline);
		details.setName(search);
		details.setId(tournaments.get(0).getId());
		Matches matches = client.matches(details);
		
		List<MatchDetails> matchlist = matches.getMatches(MatchQuery.builder().status(MatchStatus.COMPLETED).build(),new MatchesHeader().build(0,9));
		buffer.append("Opponents: ");
		
		if(matchlist.get(0).getType().equals(MatchType.DUEL))
		matchlist.stream().forEach(match -> {
			buffer.append(match.getOpponents().get(0).getParticipant().getName());
			buffer.append(" vs ");
			buffer.append(match.getOpponents().get(1).getParticipant().getName());
			buffer.append(": ");
			if(match.getOpponents().get(0).getResult().equals(Result.WIN))
			buffer.append(match.getOpponents().get(0).getParticipant().getName());
			
			if(match.getOpponents().get(1).getResult().equals(Result.WIN))
				buffer.append(match.getOpponents().get(0).getParticipant().getName());
			
			buffer.append(" WIN ");
				}
		);
		
		if(matchlist.get(0).getType().equals(MatchType.FFA)) {
			matchlist.get(0).getOpponents().stream()
					.sorted(Comparator.comparing(Opponent::getRank)).forEach(opponent -> {
				buffer.append(opponent.getParticipant().getName());
				buffer.append(": Rank ");
				buffer.append(opponent.getRank());
				buffer.append(" ");
			});
		}
		
		return buffer.toString();
	}
	
	@GetMapping("/tournaments/{game}")
	public String tournaments(@PathVariable("game") String game){
		StringBuffer buffer = new StringBuffer();
		TournamentQuery.TournamentQueryBuilder query = TournamentQuery.builder();
		//gets up to ten featured tournaments
		query.discipline(game)
				.scheduledBefore(
						LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
								LocalDate.now().getMonth().length(false)));
		
		TournamentsHeader header = new TournamentsHeader().build(0,9);
		
		List<Tournament> tournaments = client.tournaments()
				.getFeaturedTournaments(query.build(), header);
		
		if (tournaments.isEmpty()) {
			return "No featured tournaments found";
		}
		
		tournaments.stream().forEach(a -> buffer.append(a.getName()).append(", "));
		buffer.replace(buffer.length() - 2, buffer.length(), ".");
		return buffer.toString();
	}
}
