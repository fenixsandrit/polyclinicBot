package ru.kotlinUrfuEdu.polyclinic.model

import jakarta.persistence.*
import lombok.Data

@Data
@Entity(name = "doctor")
class Doctor()
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var id: Long? = null

    @Column
    private var firstName: String? = null

    @Column
    private var secondName: String? = null

    @Column
    private var type: String? = null

    @Column
    private var age: Long? = null

    constructor(id: Long?) : this()
    {
        this.id = id
    }
}