package com.ajpappas.discordquotebot;

import com.ajpappas.discordquotebot.exception.UserException;
import com.ajpappas.discordquotebot.util.MessageUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.util.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QuoteGenerator {

    @Autowired
    private GatewayDiscordClient client;

    public static final Pattern REFERENCED_MESSAGE_PATTERN = Pattern.compile("https://discord.com/channels/(?<guildid>\\d+)/(?<channelid>\\d+)/(?<messageid>\\d+)");


    public Mono<Tuple2<EmbedCreateSpec, Button>> quote(String url) {
        Mono<Message> quotedMessageMono = getReferencedMessage(url);
        Mono<Guild> quotedGuildMono = quotedMessageMono.flatMap(Message::getGuild);
        Mono<GuildChannel> quotedChannelMono = quotedMessageMono.flatMap(Message::getChannel).ofType(GuildChannel.class);

        return Mono.zip(quotedMessageMono, quotedChannelMono, quotedGuildMono)
                .map(TupleUtils.function((quotedMessage, quotedChannel, quotedGuild) -> {

                    EmbedCreateSpec embed = EmbedCreateSpec.builder()
                            .author(quotedMessage.getAuthor().map(user -> user.getUsername() + "#" + user.getDiscriminator()).orElse("Unknown"), "", quotedMessage.getAuthor().map(User::getAvatarUrl).orElse(null))
                            .description(quotedMessage.getContent())
                            //.image(quotedMessage.getAttachments().stream().findAny())
                            .timestamp(quotedMessage.getTimestamp())
                            .footer(quotedGuild.getName(), quotedGuild.getIconUrl(Image.Format.WEB_P).orElse(null))
                            .build();

                    Button button = Button.link(MessageUtil.getLink(quotedMessage), "Jump to Message");



                    return Tuples.of(embed, button);
                }));
    }

    private Mono<Message> getReferencedMessage(String url) {
        Matcher matcher = REFERENCED_MESSAGE_PATTERN.matcher(url);
        if (!matcher.find())
            return Mono.empty();

        Snowflake channel, message;

        try {
            channel = Snowflake.of(matcher.group("channelid"));
            message = Snowflake.of(matcher.group("messageid"));
        } catch (NumberFormatException ex) {
            return Mono.error(new UserException("Provided IDs in URL are not readable."));
        }

        return client.getMessageById(channel, message)
                .onErrorMap(ClientException.isStatusCode(403), error -> new UserException("I am unable to access that message."))
                .onErrorMap(ClientException.isStatusCode(404), error -> new UserException("That quote does not exist."));
    }
}
