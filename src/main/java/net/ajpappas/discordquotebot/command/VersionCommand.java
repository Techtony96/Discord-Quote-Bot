package net.ajpappas.discordquotebot.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class VersionCommand implements SlashCommand {

    @Value("${git.commit.id.describe-short}")
    private String version;

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return event.reply().withEphemeral(true).withContent(version);
    }
}
