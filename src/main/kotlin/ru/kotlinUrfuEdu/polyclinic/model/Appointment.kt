package ru.kotlinUrfuEdu.polyclinic.model

import jakarta.persistence.*
import lombok.Data
import java.sql.Date
import java.sql.Time

@Data
@Entity(name = "appointments")
class Appointment
{
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    var id: Long? = null

    @ManyToOne
    var doctor: Doctor? = null

    @ManyToOne
    var patient: Patient? = null

    @Column
    var busy = false

    @Basic
    @Temporal(TemporalType.DATE)
    var date: Date? = null

    @Basic
    @Temporal(TemporalType.TIME)
    var time: Time? = null

}