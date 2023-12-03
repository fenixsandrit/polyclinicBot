package ru.kotlinUrfuEdu.polyclinic.model

import jakarta.persistence.*
import lombok.Data

@Data
@Entity(name = "doctor")
class Doctor
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    @Column
    var firstName: String? = null

    @Column
    var secondName: String? = null

    @Column
    var type: String? = null

    @Column
    var age: Long? = null
}