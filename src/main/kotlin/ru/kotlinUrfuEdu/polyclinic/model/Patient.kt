package ru.kotlinUrfuEdu.polyclinic.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import lombok.Data

@Data
@Entity(name = "patient")
class Patient()
{
    @Id
    var id: Long? = null

    @Column
    var firstName: String? = null

    @Column
    var secondName: String? = null

    @Column
    var age: Int? = null

    constructor(id: Long?) : this()
    {
        this.id = id
    }
}