package com.example.chat.controllers;

import com.toornament.ToornamentClient;
import com.toornament.model.Discipline;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
		this.client.authorize();
		List<Discipline> disciplines = new ArrayList<>(
				this.client.disciplines().getDisciplines("disciplines=0-9"));
		StringBuffer discList = new StringBuffer();
		disciplines.stream().forEach(a ->
				discList.append(a.getName() + ": " + a.getId()+" ")
		);
		
		return discList.toString();
	}
}
