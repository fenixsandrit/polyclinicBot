package ru.kotlinUrfuEdu.polyclinic.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class TgBotService
{
    private var logger  = LoggerFactory.getLogger(this::class.java)
    private var objectMapper = ObjectMapper()

    fun getAnswer(update: Update): SendMessage
    {
        var message: SendMessage? = null
        var chatId: Long
        var userId: Long
        var data: String?

        if (update.hasMessage())
        {
            chatId = update.message.chatId
            userId = update.message.from.id
            if (update.message.hasText())
            {
                data = update.message.text
                var command: String? = null
                if (data.startsWith("/"))
                {
                    command = data.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                }
                message = processRequest(command!!, userId, chatId, java.util.Map.of<String, Any?>("data", data))
            }
        }
        else if (update.hasCallbackQuery())
        {
            chatId = update.callbackQuery.message.chatId
            userId = update.callbackQuery.from.id
            data = update.callbackQuery.data
            var command = ""
            var mapData: Map<String, Any>? = null
            if (data.contains("{"))
            {
                command = data.substring(0, data.indexOf("{") - 1)
                try
                {
                    mapData = objectMapper.readValue(data.substring(data.indexOf("{")), HashMap::class.java)["data"] as Map<String, Any>
                }
                catch (e: Exception)
                {
                    mapData = emptyMap()
                    logger.error("Произошла проблема при нажатие кнопки", e)
                }
            }
            message = processRequest(command, userId, chatId, mapData!!)
        }

        return message!!
    }


    private fun processRequest(command: String, userId: Long, chatId: Long, data: Map<String, Any>): SendMessage {
        return null
    }
}