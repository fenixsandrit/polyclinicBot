package ru.kotlinUrfuEdu.polyclinic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PolyclinicApplication

fun main(args: Array<String>) {
	runApplication<PolyclinicApplication>(*args)
}
