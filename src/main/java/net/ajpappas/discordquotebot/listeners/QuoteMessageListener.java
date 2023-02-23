package net.ajpappas.discordquotebot.listeners;

import net.ajpappas.discordquotebot.ErrorHandler;
import net.ajpappas.discordquotebot.EventFilters;
import net.ajpappas.discordquotebot.QuoteGenerator;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.spec.MessageCreateSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

@Component
public class QuoteMessageListener {

    private static final Logger log = LogManager.getLogger();

    @Autowired
    private QuoteGenerator quoteGenerator;

    @Autowired
    public QuoteMessageListener(GatewayDiscordClient client) {
        client.on(MessageCreateEvent.class)
                .filter(EventFilters.NO_BOTS)
                .filter(event -> QuoteGenerator.REFERENCED_MESSAGE_PATTERN.matcher(event.getMessage().getContent()).find())
                .flatMap(event -> handle(event)
                        //TODO set message reference to event.getMessage().getId()
                        .onErrorResume(error -> event.getMessage().getChannel().flatMap(channel -> ErrorHandler.handleError(error, channel::createMessage))))
                .subscribe();
    }

    private Mono<Void> handle(MessageCreateEvent event) {
        return quoteGenerator.quote(event.getMessage().getContent())
                .map(TupleUtils.function((embed, button) -> MessageCreateSpec.builder().addEmbed(embed).addComponent(ActionRow.of(button)).build()))
                .map(message -> message.withMessageReference(event.getMessage().getId()))
                .zipWith(event.getMessage().getChannel())
                .flatMap(TupleUtils.function((message, channel) -> channel.createMessage(message)))
                .then();
    }
}
