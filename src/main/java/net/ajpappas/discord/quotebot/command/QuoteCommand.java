package net.ajpappas.discord.quotebot.command;

import net.ajpappas.discord.quotebot.QuoteGenerator;
import net.ajpappas.discord.quotebot.exception.UserException;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

@Component
public class QuoteCommand implements SlashCommand {

    @Autowired
    private QuoteGenerator quoteGenerator;


    @Override
    public String getName() {
        return "quote";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String url = event.getOption("link")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); // link parameter is required

        if (!QuoteGenerator.REFERENCED_MESSAGE_PATTERN.matcher(url).matches()) {
            return Mono.error(new UserException("The provided URL does not match the expected format"));
        }

        return quoteGenerator.quote(url)
                .flatMap(TupleUtils.function((embed, button) -> event.reply()
                        .withEmbeds(embed)
                        .withComponents(ActionRow.of(button)))
                );
    }
}
