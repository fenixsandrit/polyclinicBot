package ru.kotlinUrfuEdu.polyclinic.appEventListener

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.kotlinUrfuEdu.polyclinic.constant.DOCTOR_TYPES
import ru.kotlinUrfuEdu.polyclinic.model.Appointment
import ru.kotlinUrfuEdu.polyclinic.model.Doctor
import ru.kotlinUrfuEdu.polyclinic.repo.AppointmentsRepo
import ru.kotlinUrfuEdu.polyclinic.repo.DoctorRepo
import java.sql.Date
import java.sql.Time

@Component
class DBFillingEventListener @Autowired constructor(private var appointmentsRepo: AppointmentsRepo, private var doctorRepo: DoctorRepo)
{

    @EventListener
    fun handleContextStart(cse: ContextRefreshedEvent?)
    {
        println("Handling context refreshed event.")

        /*val firstName = "Марья"
        val secondName = "Ивановна"
        val doctors: Array<String> = DOCTOR_TYPES
        var counter = 0

        for (i in doctors.indices) {
            for (j in 0..2) {
                counter++

                val d = Doctor()
                d.firstName = firstName + counter
                d.secondName = (secondName + counter)
                d.age = (30L)
                d.type = (doctors[i])
                doctorRepo.save(d)

                val a1 = Appointment()
                a1.doctor = (d)
                a1.date = (Date(123, 6, 15))
                a1.time = (Time(10, 30, 0))
                appointmentsRepo.save(a1)

                val a2 = Appointment()
                a2.doctor = (d)
                a2.date = (Date(123, 6, 16))
                a2.time = (Time(10, 30, 0))
                appointmentsRepo.save(a2)

                val a3 = Appointment()
                a3.doctor = (d)
                a3.date = (Date(123, 6, 17))
                a3.time = (Time(10, 30, 0))
                appointmentsRepo.save(a3)
            }
        }*/
    }
}