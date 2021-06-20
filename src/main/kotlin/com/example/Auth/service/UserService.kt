package com.example.Auth.service


import com.example.Auth.dtos.RegisterDto
import com.example.Auth.model.User
import com.example.Auth.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {

    fun save(user: User): User {
        return userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun getById(id: Long): User{
        return userRepository.getById(id)
    }
}