package com.example.Auth.repository

import com.example.Auth.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User,Long> {

    fun findByEmail(email:String): User?
}