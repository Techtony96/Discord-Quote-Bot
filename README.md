# Discord Quote Bot

## Setup

docker-compose.yml
```yml
version: '3.7'

services:
  quote-bot:
    image: ghcr.io/techtony96/discord-quote-bot:master
    restart: unless-stopped
    environment:
      - BOT_TOKEN=foo
```