package ru.kotlinUrfuEdu.polyclinic.repo

import org.springframework.data.repository.CrudRepository
import ru.kotlinUrfuEdu.polyclinic.model.Appointment

interface AppointmentsRepo : CrudRepository<Appointment, Long>
{
    fun findAppointmentsByDoctor_IdAndBusy(doctorId: Long?, isBusy: Boolean): List<Appointment?>?
    fun findAppointmentsByPatient_Id(patientId: Long?): List<Appointment?>?
}