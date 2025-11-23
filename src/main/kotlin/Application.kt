package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import routes.configureChatRoutes
import service.ChatService
import java.time.Duration

fun main() {
    // 서버 시작 전 타이머 가동
    ChatService.startTimer()

    // 서버 실행
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 웹소켓 플러그인 설치
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // 채팅 라우트 설정 적용
    configureChatRoutes()
}