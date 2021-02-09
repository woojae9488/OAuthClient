package com.example.oauth.util

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.UnsupportedOperationException
import java.util.*
import javax.servlet.http.Cookie

class CookieUtils private constructor() {

    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        fun getCookie(request: HttpServletRequest, key: String): Cookie? {
            val cookies = request.cookies
            return cookies?.firstOrNull { it.name == key }
        }

        fun setCookie(response: HttpServletResponse, key: String, value: String, maxAge: Int) {
            val cookie = Cookie(key, value).apply {
                this.path = "/"
                this.isHttpOnly = true
                this.maxAge = maxAge
            }
            response.addCookie(cookie)
        }

        fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
            val cookies = request.cookies
            cookies?.firstOrNull { it.name == name }?.let {
                it.value = ""
                it.path = "/"
                it.maxAge = 0
                response.addCookie(it)
            }
        }
    }

}