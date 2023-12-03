package ru.kotlinUrfuEdu.polyclinic.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import ru.kotlinUrfuEdu.polyclinic.config.BOT_INFO
import ru.kotlinUrfuEdu.polyclinic.constant.*
import ru.kotlinUrfuEdu.polyclinic.model.Appointment
import ru.kotlinUrfuEdu.polyclinic.model.Doctor
import ru.kotlinUrfuEdu.polyclinic.model.Patient
import ru.kotlinUrfuEdu.polyclinic.repo.AppointmentsRepo
import ru.kotlinUrfuEdu.polyclinic.repo.DoctorRepo
import ru.kotlinUrfuEdu.polyclinic.repo.PatientRepo

@Service
class TgBotService
{
    private var logger  = LoggerFactory.getLogger(this::class.java)
    private var objectMapper = ObjectMapper()
    private var buttonProvider: ButtonProvider
    private var appointmentsRepo: AppointmentsRepo
    private var doctorRepo: DoctorRepo
    private var patientRepo: PatientRepo

    private val userStates: MutableMap<Long, String> = HashMap()
    private val stateToCommand: MutableMap<String, String> = mapOf(SET_FIRST_NAME_STATE to SET_FIRST_NAME_COMMAND,
                                                                   SET_SECOND_NAME_STATE to SET_SECOND_NAME_COMMAND,
                                                                   SET_AGE_STATE to SET_AGE_COMMAND,
                                                                   GET_MAIN_MENU_STATE to GET_MAIN_MENU_COMMAND) as MutableMap<String, String>

    @Autowired
    constructor(buttonProvider: ButtonProvider, appointmentsRepo: AppointmentsRepo, doctorRepo: DoctorRepo, patientRepo: PatientRepo)
    {
        this.buttonProvider = buttonProvider
        this.appointmentsRepo = appointmentsRepo
        this.doctorRepo = doctorRepo
        this.patientRepo = patientRepo
    }

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


    private fun processRequest(command: String?, userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        var curCommand: String? = command
        if (curCommand == null)
        {
            curCommand = if (userStates.containsKey(userId)) stateToCommand[userStates[userId]] else null
        }

        return when (curCommand)
        {
            START_COMMAND -> startProcess(userId, chatId, data)
            GET_MAIN_MENU_COMMAND -> mainMenuProcess(userId, chatId, data, "")
            GET_BOT_INFO_COMMAND -> getBotInfoProcess(userId, chatId, data)
            EDIT_PROFILE_COMMAND -> profileEditingProcess(userId, chatId, data)
            DELETE_PROFILE_COMMAND -> deleteProfileProcess(userId, chatId, data)
            SET_FIRST_NAME_COMMAND -> setFirstNameProcess(userId, chatId, data)
            SET_SECOND_NAME_COMMAND -> setSecondNameProcess(userId, chatId, data)
            SET_AGE_COMMAND -> setAgeProcess(userId, chatId, data)
            LIST_AVAILABLE_DOCTOR_TYPES_COMMAND -> getListAvailableDoctorTypesProcess(userId, chatId, data)
            LIST_DOCTORS_BY_TYPE_COMMAND -> getListDoctorByTypeProcess(userId, chatId, data)
            LIST_AVAILABLE_RECORDS_BY_DOCTOR_COMMAND -> getListAvailableRecordsByDoctor(userId, chatId, data)
            RECORD_TO_APPOINTMENT_BY_ID_COMMAND -> recordToAppointmentById(userId, chatId, data)
            GET_MY_RECORDS_LIST_COMMAND -> listOfRecordsProcess(userId, chatId, data)
            GET_APPOINTMENT_BY_ID_COMMAND -> getRecordByIdProcess(userId, chatId, data)
            CANCEL_APPOINTMENT_BY_ID_COMMAND -> cancelRecordByIdProcess(userId, chatId, data)
            SEE_PROFILE_COMMAND -> seeProfileProcess(userId, chatId, data)
            else -> unrecognizedCommandProcess(userId, chatId, data)
        }
    }

    private fun seeProfileProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        val p: Patient = patientRepo.findById(userId).get()
        message.chatId = chatId.toString()
        message.text = "Имя: ${p.firstName}\nФамилия: ${p.secondName}\nВозраст: ${p.age}"

        return message
    }

    private fun getRecordByIdProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = chatId.toString()
        val appointmentId = data["appointmentId"] as Int
        val appointment: Appointment = appointmentsRepo.findById(appointmentId.toLong()).get()
        message.text = "Запись\n" +
                        "${appointment.date} ${appointment.time} ${appointment.doctor?.secondName} ${appointment.doctor?.firstName}" +
                        if (appointment.confirmed) "\nЗапись подтверждена" else ""

        message.replyMarkup = buttonProvider.getDeleteRecordButtons(appointmentId.toString())
        return message
    }

    private fun cancelRecordByIdProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = "Вы успешно отменили запись"
        val appointmentId: Int = (data["appointmentId"] as String).toInt()
        val appointment: Appointment = appointmentsRepo.findById(appointmentId.toLong()).get()
        appointment.patient = null
        appointment.busy = false
        appointment.confirmed = false
        appointmentsRepo.save(appointment)

        return message
    }

    private fun recordToAppointmentById(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = chatId.toString()
        val appointmentId = data["appointmentId"] as Int
        val appointment: Appointment = appointmentsRepo.findById(appointmentId.toLong()).get()

        if (appointment.busy)
        {
            message.text = "Кто-то записался раньше вас..."
        }
        else
        {
            message.text = "Вы успешно записались"
            val patient: Patient = patientRepo.findById(userId).get()
            appointment.patient = patient
            appointment.busy = true
            appointmentsRepo.save(appointment)
        }

        return message
    }

    private fun getListAvailableRecordsByDoctor(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = java.lang.Long.toString(chatId)
        message.text = "Пока таких врачей нет"
        val doctorId = data["doctorId"] as Int
        val list: List<Appointment> = appointmentsRepo.findAppointmentsByDoctor_IdAndBusy(doctorId.toLong(), false) as List<Appointment>

        if (list.isNotEmpty())
        {
            message.replyMarkup = buttonProvider.listOfDoctorAppointments(list)
            message.text = "Выберите удобное время записи"
        }

        return message
    }

    private fun getBotInfoProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = BOT_INFO

        return message
    }

    private fun unrecognizedCommandProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = java.lang.Long.toString(chatId)
        message.text = "Команда не опознана"

        return message
    }

    private fun deleteProfileProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val appointments: List<Appointment> = appointmentsRepo.findAppointmentsByPatient_Id(userId) as List<Appointment>
        appointments.forEach {
            it.patient = null
            it.busy = false
            it.confirmed = false

            appointmentsRepo.save(it)
        }
        patientRepo.deleteById(userId)
        userStates.remove(userId)

        val message = SendMessage()
        message.chatId = chatId.toString()
        message.replyMarkup = buttonProvider.inlineMarkup()
        message.text = "Ваш профиль успешно удалён"

        return message
    }

    private fun listOfRecordsProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = "Ваши активные записи:"
        val list: List<Appointment> = appointmentsRepo.findAppointmentsByPatient_Id(userId) as List<Appointment>

        if (list.isNotEmpty())
        {
            message.replyMarkup = buttonProvider.getPatientRecordsButtons(list)
        }

        return message
    }

    private fun getListAvailableDoctorTypesProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = "Выберите тип врача"
        message.replyMarkup = buttonProvider.listOfDoctorTypesButtons()

        return message
    }

    private fun getListDoctorByTypeProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        val message = SendMessage()
        message.chatId = chatId.toString()
        val doctorType = data["doctorType"] as String
        val list: List<Doctor> = doctorRepo.findAllByType(doctorType) as List<Doctor>

        if (list.isEmpty())
        {
            message.text = "К сожалению, пока таких врачей нет"
        }
        else
        {
            message.text = "Выберите врача"
            message.replyMarkup = buttonProvider.listOfDoctorButtons(list)
        }

        return message
    }

    private fun startProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        if (patientRepo.existsById(userId))
        {
            return mainMenuProcess(userId, chatId, data, "")
        }
        val patient = Patient(userId)
        patientRepo.save(patient)

        return profileEditingProcess(userId, chatId, data)
    }

    private fun profileEditingProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage?
    {
        userStates[userId] = SET_FIRST_NAME_STATE
        val sendMessage = SendMessage()
        sendMessage.chatId = chatId.toString()
        sendMessage.text = "Введите имя"

        return sendMessage
    }

    private fun setFirstNameProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage
    {
        val patient: Patient = patientRepo.findById(userId).get()
        patient.firstName = data["data"] as String
        patientRepo.save(patient)
        userStates[userId] = SET_SECOND_NAME_STATE
        val sendMessage = SendMessage()
        sendMessage.chatId = chatId.toString()
        sendMessage.text = "Введите фамилию"

        return sendMessage
    }

    private fun setSecondNameProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage
    {
        val patient: Patient = patientRepo.findById(userId).get()
        patient.secondName = data["data"] as String
        patientRepo.save(patient)
        userStates[userId] = SET_AGE_STATE
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = "Введите возраст"

        return message
    }

    private fun setAgeProcess(userId: Long, chatId: Long, data: Map<String, Any>): SendMessage
    {
        val patient: Patient = patientRepo.findById(userId).get()
        var age: Int = -1
        try
        {
            age = (data["data"] as String).toInt()
            if (age < 16 || age > 120) throw java.lang.Exception()
        }
        catch (e: java.lang.Exception)
        {
            val messageExc = SendMessage()
            messageExc.chatId = chatId.toString()
            messageExc.text = "Вы ввели некорректный возраст, повторите"

            return messageExc
        }

        patient.age = age
        patientRepo.save(patient)
        val message = mainMenuProcess(userId, chatId, data, "")
        message.text = "Регистрация завершена"

        return message
    }

    private fun mainMenuProcess(userId: Long, chatId: Long, data: Map<String, Any>, textMessage: String): SendMessage
    {
        userStates[userId] = GET_MAIN_MENU_STATE
        val sendMessage = SendMessage()
        sendMessage.text = if (textMessage.isEmpty()) "Главное меню" else textMessage
        val buttons: InlineKeyboardMarkup = buttonProvider.mainMenuButtons()
        sendMessage.replyMarkup = buttons
        sendMessage.chatId = chatId.toString()

        return sendMessage
    }
}