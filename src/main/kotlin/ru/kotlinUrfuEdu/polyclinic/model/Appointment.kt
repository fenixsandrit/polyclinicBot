package ru.kotlinUrfuEdu.polyclinic.model

import jakarta.persistence.*
import lombok.Data
import java.sql.Date
import java.sql.Time

@Data
@Entity(name = "appointments")
class Appointment()
{
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private var id: Long? = null

    @ManyToOne
    private var doctor: Doctor? = null

    @ManyToOne
    private var patient: Patient? = null

    @Column
    private var busy = false

    @Basic
    @Temporal(TemporalType.DATE)
    private var date: Date? = null

    @Basic
    @Temporal(TemporalType.TIME)
    private var time: Time? = null

    constructor(id: Long?) : this()
    {
        this.id = id
    }
}