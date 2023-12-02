package ru.kotlinUrfuEdu.polyclinic.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.kotlinUrfuEdu.polyclinic.constant.*
import ru.kotlinUrfuEdu.polyclinic.model.Appointment
import ru.kotlinUrfuEdu.polyclinic.model.Doctor
import java.util.*

@Service
class ButtonProvider
{
    private val objectMapper = ObjectMapper()
    private val START_BUTTON = InlineKeyboardButton("Start")
    private val HELP_BUTTON = InlineKeyboardButton("Помощь")
    private val THERAPIST_BUTTON = InlineKeyboardButton("Терапевт")
    private val SURGEON_BUTTON = InlineKeyboardButton("Хирург")
    private val CARDIOLOGIST_BUTTON = InlineKeyboardButton("Кардиолог")
    private val DENTIST_BUTTON = InlineKeyboardButton("Стоматолог")
    private val DERMATOLOGIST_BUTTON = InlineKeyboardButton("Дерматолог")
    private val TRAUMATOLOGIST_BUTTON = InlineKeyboardButton("Травматолог")
    private val UROLOGIST_BUTTON = InlineKeyboardButton("Уролог")
    private val GYNECOLOGIST_BUTTON = InlineKeyboardButton("Гинеколог")

    fun inlineMarkup(): InlineKeyboardMarkup
    {
        START_BUTTON.callbackData = getJsonForButton(START_COMMAND, emptyMap())

        //HELP_BUTTON.setCallbackData(HELP_COMMAND);
        //List<InlineKeyboardButton> rowInline = List.of(START_BUTTON, HELP_BUTTON);

        val rowInline = java.util.List.of(START_BUTTON)
        val rowsInLine = java.util.List.of(rowInline)
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = rowsInLine

        return markupInline
    }

    fun mainMenuButtons(): InlineKeyboardMarkup
    {
        val a = InlineKeyboardButton("Записаться")
        val b = InlineKeyboardButton("Мои записи")
        val c = InlineKeyboardButton("Удалить профиль")
        val d = InlineKeyboardButton("Редактировать профиль")
        val f = InlineKeyboardButton("Просмотреть профиль")
        a.callbackData = getJsonForButton(LIST_AVAILABLE_DOCTOR_TYPES_COMMAND, emptyMap())
        b.callbackData = getJsonForButton(GET_MY_RECORDS_LIST_COMMAND, emptyMap())
        c.callbackData = getJsonForButton(DELETE_PROFILE_COMMAND, emptyMap())
        d.callbackData = getJsonForButton(EDIT_PROFILE_COMMAND, emptyMap())
        f.callbackData = getJsonForButton(SEE_PROFILE_COMMAND, emptyMap())
        val rowInlineFirst = listOf(a, b)
        val rowInlineSecond = listOf(d, f)
        val rowInlineThird = listOf(c)
        val rowsInLine = listOf(rowInlineFirst, rowInlineSecond, rowInlineThird)
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = rowsInLine

        return markupInline
    }

    fun listOfDoctorTypesButtons(): InlineKeyboardMarkup
    {
        THERAPIST_BUTTON.callbackData = getJsonForDoctorsListButton(THERAPIST)
        SURGEON_BUTTON.callbackData = getJsonForDoctorsListButton(SURGEON)
        val l1 = listOf(THERAPIST_BUTTON, SURGEON_BUTTON)

        CARDIOLOGIST_BUTTON.callbackData = getJsonForDoctorsListButton(CARDIOLOGIST)
        DENTIST_BUTTON.callbackData = getJsonForDoctorsListButton(DENTIST)
        val l2 = listOf(CARDIOLOGIST_BUTTON, DENTIST_BUTTON)

        DERMATOLOGIST_BUTTON.callbackData = getJsonForDoctorsListButton(DERMATOLOGIST)
        TRAUMATOLOGIST_BUTTON.callbackData = getJsonForDoctorsListButton(TRAUMATOLOGIST)
        val l3 = listOf(DERMATOLOGIST_BUTTON, TRAUMATOLOGIST_BUTTON)

        UROLOGIST_BUTTON.callbackData = getJsonForDoctorsListButton(UROLOGIST)
        GYNECOLOGIST_BUTTON.callbackData = getJsonForDoctorsListButton(GYNECOLOGIST)
        val l4 = listOf(UROLOGIST_BUTTON, GYNECOLOGIST_BUTTON)

        val rowsInLine = listOf(l1, l2, l3, l4)
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = rowsInLine

        return markupInline
    }

    fun listOfDoctorButtons(doctors: List<Doctor?>): InlineKeyboardMarkup
    {
        val buttons: MutableList<List<InlineKeyboardButton>> = ArrayList()
        doctors.forEach {
            val button = InlineKeyboardButton(it?.secondName + " " + it?.firstName)
            button.callbackData = getJsonForButton(LIST_AVAILABLE_RECORDS_BY_DOCTOR_COMMAND, java.util.Map.of("doctorId", it?.id))
            buttons.add(listOf(button))
        }
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = buttons

        return markupInline
    }

    fun listOfDoctorAppointments(appointments: List<Appointment?>): InlineKeyboardMarkup? {
        val buttons: MutableList<List<InlineKeyboardButton>> = ArrayList()

        appointments.forEach {
            val button = InlineKeyboardButton(it?.date.toString() + " " + it?.time.toString())
            button.callbackData = getJsonForButton(RECORD_TO_APPOINTMENT_BY_ID_COMMAND, java.util.Map.of("appointmentId", it?.id))
            buttons.add(listOf(button))
        }
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = buttons

        return markupInline
    }

    fun getPatientRecordsButtons(appointments: List<Appointment?>): InlineKeyboardMarkup?
    {
        val buttons: MutableList<List<InlineKeyboardButton>> = ArrayList()
        appointments.forEach {
            val button = InlineKeyboardButton(it?.date.toString() + " " + it?.time.toString() + " "
                                            + it?.doctor?.firstName + " " + it?.doctor?.secondName)
            button.callbackData = getJsonForButton(GET_APPOINTMENT_BY_ID_COMMAND, mapOf("appointmentId" to it?.id))
            buttons.add(listOf(button))
        }
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = buttons

        return markupInline
    }

    fun getDeleteRecordButtons(appointmentId: String?): InlineKeyboardMarkup?
    {
        val buttons: MutableList<List<InlineKeyboardButton>> = ArrayList()
        val button = InlineKeyboardButton("Удалить запись")
        button.callbackData = getJsonForButton(CANCEL_APPOINTMENT_BY_ID_COMMAND, mapOf("appointmentId" to appointmentId))
        buttons.add(listOf(button))
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = buttons

        return markupInline
    }

    private fun getJsonForDoctorsListButton(doctorType: String): String?
    {
        return getJsonForButton(LIST_DOCTORS_BY_TYPE_COMMAND, java.util.Map.of<String?, Any?>("doctorType", doctorType))
    }

    private fun getJsonForButton(command: String, data: Map<String?, Any?>): String?
    {
        return try
        {
            command + " " + objectMapper.writeValueAsString(java.util.Map.of("data", data))
        }
        catch (e: JsonProcessingException)
        {
            throw RuntimeException(e)
        }
    }
}