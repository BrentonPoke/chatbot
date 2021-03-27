package com.example.chat.views.chat;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.artur.Avataaar;

public class EsportsCard extends VerticalLayout {
	
	public EsportsCard() {
	}
	public void addMessage(String from, Avataaar avatar, String ans,boolean isCurrentUser) {
		Span fromContainer = new Span(new Text(from));
		fromContainer.addClassName(getClass().getSimpleName() + "-name");
		
		Div textContainer = new Div(new Text(ans));
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
		
		line.getElement().callJsFunction("scrollIntoView");}
	
}
