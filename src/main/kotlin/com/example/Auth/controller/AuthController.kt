package com.example.Auth.controller

import com.example.Auth.dtos.LoginDto
import com.example.Auth.dtos.Message
import com.example.Auth.dtos.RegisterDto
import com.example.Auth.model.User
import com.example.Auth.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.tomcat.util.http.parser.Cookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/JWT")
class AuthController(val userService:  UserService) {

    @PostMapping("/register")
    fun register(@RequestBody body: RegisterDto): ResponseEntity<User> {
        val user = User()
        user.name = body.name
        user.email = body.email
        user.password = body.password
        return ResponseEntity.ok(userService.save(user))
    }

    @PostMapping("/login")
    fun login(@RequestBody body: LoginDto, response: HttpServletResponse): ResponseEntity<Any>{
        val user = userService.findByEmail(body.email)
                ?: return ResponseEntity.badRequest().body(Message("user not found"))

        if(!user.comparePassword(body.password)){
            return ResponseEntity.badRequest().body(Message("invalid password"))
        }

        val issuer = user.id.toString()

        val jwt = Jwts.builder()
                .setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) //1day
                .signWith(SignatureAlgorithm.HS512 , "secret").compact()

        val cookie = javax.servlet.http.Cookie("jwt",jwt)
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("Success"))
    }

    @GetMapping("/user")
    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any>{
        try{
            if(jwt == null){
                return ResponseEntity.status(401).body(Message("unauthenticated"))
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body

            return ResponseEntity.ok(userService.getById(body.issuer.toLong()))
        } catch (e: Exception){
            return ResponseEntity.status(401).body(Message("unauthenticated"))
        }
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any>{
        val cookie = javax.servlet.http.Cookie("jwt","")
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity.ok(Message("Success"))
    }
}