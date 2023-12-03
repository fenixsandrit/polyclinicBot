package ru.kotlinUrfuEdu.polyclinic.config

import lombok.Data
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
@Data
class BotConfig
{
    @Value("\${bot.name}")
    lateinit var botName: String

    @Value("\${bot.token}")
    lateinit var token: String
}

const val BOT_INFO = "Бот записывает вас на прием к врачу";

