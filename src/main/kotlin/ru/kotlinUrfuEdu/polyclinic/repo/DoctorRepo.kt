package ru.kotlinUrfuEdu.polyclinic.repo

import org.springframework.data.repository.CrudRepository
import ru.kotlinUrfuEdu.polyclinic.model.Doctor

interface DoctorRepo : CrudRepository<Doctor, Long>
{
    fun findAllByType(type: String?): List<Doctor?>?
}