package ru.kotlinUrfuEdu.polyclinic.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.kotlinUrfuEdu.polyclinic.model.Patient

@Repository
interface PatientRepo : CrudRepository<Patient, Long>
{
}