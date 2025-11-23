package service

import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

class ChatService {

    private val userCounter = AtomicInteger(0)

    // 접속자 관리
    private val connections = Collections.synchronizedSet(LinkedHashSet<DefaultWebSocketSession>())

    // 유저 입장
    fun joinUser(session: DefaultWebSocketSession): String {
        connections += session
        val id = userCounter.incrementAndGet()
        val prefix = listOf("픽셀", "레트로", "8비트", "용사", "마법사").random()
        return "${prefix}플레이어$id"
    }

    // 유저 퇴장
    fun leaveUser(session: DefaultWebSocketSession) {
        connections -= session
    }

    // 전체 방송
    suspend fun broadcast(message: String) {
        connections.forEach {
            try { it.send(message) } catch (e: Exception) {}
        }
    }
}