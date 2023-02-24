package net.ajpappas.discord.quotebot;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.function.Predicate;

public class EventFilters {

    public static final Predicate<MessageCreateEvent> NO_BOTS = event -> event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false);

}
