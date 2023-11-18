package ru.kotlinUrfuEdu.polyclinic

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import ru.kotlinUrfuEdu.polyclinic.controller.TelegramBot

@Component
@Slf4j
class BotInitializer
{
    @Autowired
    private lateinit var telegramBot: TelegramBot

    private var logger  = LoggerFactory.getLogger(this::class.java)

    @EventListener(ContextRefreshedEvent::class)
    fun init()
    {
        try
        {
            val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
            telegramBotsApi.registerBot(telegramBot)
        }
        catch (e: TelegramApiException)
        {
            logger.error("Bot initialization exception", e)
        }
    }
}