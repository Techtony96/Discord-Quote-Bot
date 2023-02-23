package com.ajpappas.discordquotebot;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DiscordQuoteBot implements CommandLineRunner {

    @Value("${bot.token}")
    private String botToken;

    public static void main(String[] args) {
        SpringApplication.run(DiscordQuoteBot.class, args);
    }

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        return DiscordClientBuilder.create(botToken).build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online(ClientActivity.watching("your best quotes")))
                .login()
                .block();
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }


    @Override
    public void run(String... args) throws Exception {
        // Prevent Spring from shutting down immediately after start up
        Thread.currentThread().join();
    }
}
