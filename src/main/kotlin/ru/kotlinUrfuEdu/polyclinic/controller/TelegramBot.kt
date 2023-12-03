package ru.kotlinUrfuEdu.polyclinic.controller

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.kotlinUrfuEdu.polyclinic.config.BotConfig
import ru.kotlinUrfuEdu.polyclinic.constant.GET_MAIN_MENU_COMMAND
import ru.kotlinUrfuEdu.polyclinic.constant.START_COMMAND
import ru.kotlinUrfuEdu.polyclinic.service.TgBotService

@Slf4j
@Component
class TelegramBot (): TelegramLongPollingBot()
{
    private final val menuCommands = listOf(
        BotCommand(START_COMMAND, "start bot"),
        BotCommand(GET_MAIN_MENU_COMMAND, "main menu")
    )
    private lateinit var config: BotConfig
    private var logger  = LoggerFactory.getLogger(this::class.java)
    private lateinit var tgBotService: TgBotService

    @Autowired
    constructor(config: BotConfig, tgBotService: TgBotService) : this()
    {
        this.config = config
        this.tgBotService = tgBotService
    }

    init
    {
        try
        {
            execute(SetMyCommands(menuCommands, BotCommandScopeDefault(), null))
        }
        catch (e: TelegramApiException)
        {
            logger.error("Init error:" + e.message)
        }
    }

    override fun getBotToken(): String = config.token

    override fun getBotUsername(): String = config.botName

    override fun onUpdateReceived(update: Update)
    {
        val backMessage = tgBotService.getAnswer(update)
        sendMessage(backMessage)
    }

    fun sendMessage(message: SendMessage?)
    {
        try
        {
            execute(message)
        }
        catch (e: TelegramApiException)
        {
            logger.error("Sending message error:", e.message)
        }
    }
}