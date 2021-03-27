package com.example.chat.views.chat;

import com.github.appreciated.card.Card;
import com.github.appreciated.card.content.IconItem;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import org.vaadin.artur.Avataaar;

public class MessageList extends Div {

    public MessageList() {
    
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
    
    public void esportsMessage(String from, Avataaar avatar, String ans,boolean isCurrentUser, String imagePath, String title, String description) {
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
        
        Div textContainer = new Div(new Text(ans), card);
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
