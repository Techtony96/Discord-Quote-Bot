package net.ajpappas.discord.quotebot.command;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.Message;
import net.ajpappas.discord.common.command.MessageCommand;
import net.ajpappas.discord.quotebot.service.QuoteGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

@Component
public class QuoteMessageCommand implements MessageCommand {

    @Autowired
    private QuoteGenerator quoteGenerator;

    @Override
    public String getName() {
        return "Quote This";
    }

    @Override
    public Mono<Void> handle(MessageInteractionEvent event) {
        Mono<Message> quoted = event.getTargetMessage();
        return quoted.flatMap(msg -> quoteGenerator.quote(msg))
                .flatMap(TupleUtils.function((embed, button) -> event.reply()
                        .withEmbeds(embed)
                        .withComponents(ActionRow.of(button)))
                );
    }
}
