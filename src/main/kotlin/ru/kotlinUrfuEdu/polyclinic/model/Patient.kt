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
    private var id: Long? = null

    @Column
    private var firstName: String? = null

    @Column
    private var secondName: String? = null

    @Column
    private var age: Int? = null

    constructor(id: Long?) : this()
    {
        this.id = id
    }
}