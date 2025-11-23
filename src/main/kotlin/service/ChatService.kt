package service

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.LinkedHashSet

class ChatService {

    // 5분 타이머
    val boomTime = AtomicLong(System.currentTimeMillis() + (5 * 60 * 1000))
    private val userCounter = AtomicInteger(0)

    // 타이머 시작
    fun startTimer() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val timeLeft = boomTime.get() - System.currentTimeMillis()

                // 시간이 다 됐을 때
                if (timeLeft <= 0) {
                    broadcast("GAME OVER! 모든 연결이 종료됩니다.")
                    delay(2000)
                    connections.forEach {
                        try { it.close(CloseReason(CloseReason.Codes.NORMAL, "Time over")) } catch (e: Exception) {}
                    }
                    connections.clear()
                    break
                }

                // 30초, 10초 전 경고
                if ((timeLeft < 31000 && timeLeft > 30000) || (timeLeft < 11000 && timeLeft > 10000)) {
                    val sec = timeLeft / 1000
                    broadcast("⚠ WARNING: TIME LEFT $sec SECONDS! ⚠")
                }
                delay(1000)
            }
        }
    }

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

    // 시간 연장
    suspend fun extendTime() {
        val newTime = boomTime.addAndGet(30 * 1000)
        broadcast("SYNC_TIME:$newTime")
        broadcast("BONUS STAGE! 누군가가 '레벨업' 아이템을 사용했습니다! (+30s)")
    }
}