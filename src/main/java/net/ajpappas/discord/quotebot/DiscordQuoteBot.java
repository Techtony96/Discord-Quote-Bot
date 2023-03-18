package net.ajpappas.discord.quotebot;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication(scanBasePackages = "net.ajpappas.discord")
public class DiscordQuoteBot implements CommandLineRunner {

    private static final Logger log = LogManager.getLogger();

    @Value("${bot.token}")
    private String botToken;

    @Value("${git.commit.id.describe-short}")
    private String botVersion;

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

    @PostConstruct
    public void startupApplication() {
        log.info("Running version {}", botVersion);
    }

    @Override
    public void run(String... args) throws Exception {
        // Prevent Spring from shutting down immediately after start up
        Thread.currentThread().join();
    }
}
