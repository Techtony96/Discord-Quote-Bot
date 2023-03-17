# Discord Quote Bot

## Invite URL
(Replace `INSERT_CLIENT_ID_HERE` with your app's client ID)  
https://discord.com/oauth2/authorize?client_id=INSERT_CLIENT_ID_HERE&scope=bot&permissions=412317239296

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
