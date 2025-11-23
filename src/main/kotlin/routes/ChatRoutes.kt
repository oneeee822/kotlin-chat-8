package routes

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import service.ChatService

fun Application.configureChatRoutes() {
    routing {
        // HTML/CSS/JS
        get("/") {
            call.respondHtml {
                head {
                    title { +" 👾픽셀 익명톡방👾" }
                    link(rel = "stylesheet", href = "https://fonts.googleapis.com/css2?family=VT323&display=swap")
                    style {
                        unsafe {
                            +"""
                                :root {
                                    --bg-pink: #F8D2DB; --bg-lavender: #D7B8E8; --bg-blue: #A9E0E8;
                                    --bg-mint: #C1EFEF; --bg-white: #FFFBFO; --text-color: #54436B;
                                    --pixel-border: 4px solid #54436B;
                                }
                                body {
                                    background: linear-gradient(180deg, var(--bg-blue) 0%, var(--bg-lavender) 50%, var(--bg-pink) 100%);
                                    font-family: 'VT323', monospace; color: var(--text-color); margin: 0; padding: 20px;
                                    height: 100vh; box-sizing: border-box; display: flex; justify-content: center; align-items: center;
                                    background-image: linear-gradient(rgba(255,255,255,.2) 2px, transparent 2px), linear-gradient(90deg, rgba(255,255,255,.2) 2px, transparent 2px);
                                    background-size: 20px 20px;
                                }
                                .game-container {
                                    background-color: var(--bg-white); width: 100%; max-width: 600px; height: 90vh;
                                    border: var(--pixel-border); box-shadow: 10px 10px 0px rgba(84, 67, 107, 0.3);
                                    padding: 20px; display: flex; flex-direction: column; border-radius: 8px;
                                }
                                h1 { text-align: center; margin: 0 0 15px 0; font-size: 2.5em; text-transform: uppercase; letter-spacing: 2px; text-shadow: 2px 2px 0px var(--bg-pink); }
                                .pixel-icon { font-size: 1.2em; margin-right: 5px; vertical-align: middle; }
                                #topBar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding: 10px; background-color: var(--bg-mint); border: 2px solid var(--text-color); }
                                #timerBox { font-size: 1.5em; font-weight: bold; }
                                #hackBtn { background-color: var(--bg-pink); color: var(--text-color); border: 2px solid var(--text-color); padding: 5px 15px; font-size: 1.2em; font-family: 'VT323', monospace; cursor: pointer; box-shadow: 4px 4px 0px var(--text-color); font-weight: bold; }
                                #hackBtn:hover { transform: translate(2px, 2px); box-shadow: 2px 2px 0px var(--text-color); }
                                #chatBox { flex-grow: 1; overflow-y: scroll; padding: 15px; margin-bottom: 15px; background-color: #fff; border: 2px solid var(--text-color); background-image: linear-gradient(transparent 50%, rgba(84, 67, 107, 0.05) 50%); background-size: 100% 4px; display: flex; flex-direction: column; }
                                ::-webkit-scrollbar { width: 12px; }
                                ::-webkit-scrollbar-track { background: var(--bg-mint); border-left: 2px solid var(--text-color); }
                                ::-webkit-scrollbar-thumb { background: var(--bg-lavender); border: 2px solid var(--text-color); }
                                .message-bubble { border: 2px solid var(--text-color); padding: 8px 12px; margin-bottom: 10px; font-size: 1.2em; box-shadow: 3px 3px 0px rgba(84, 67, 107, 0.2); width: fit-content; max-width: 80%; word-wrap: break-word; }
                                .my-msg { align-self: flex-end; background-color: var(--bg-pink); border-bottom-right-radius: 0; }
                                .other-msg { align-self: flex-start; background-color: var(--bg-blue); border-bottom-left-radius: 0; }
                                .system-msg { align-self: center; width: 100%; max-width: 100%; text-align: center; background: none; border: none; box-shadow: none; color: var(--text-color); font-weight: bold; border-top: 2px dashed var(--text-color); border-bottom: 2px dashed var(--text-color); padding: 5px 0; }
                                .input-area { display: flex; gap: 10px; }
                                input { flex-grow: 1; padding: 10px; border: 2px solid var(--text-color); font-size: 1.3em; font-family: 'VT323', monospace; outline: none; background-color: #fff; }
                                input:focus { background-color: var(--bg-mint); }
                                button.sendBtn { background-color: var(--bg-lavender); color: var(--text-color); border: 2px solid var(--text-color); padding: 0 20px; font-size: 1.5em; font-family: 'VT323', monospace; cursor: pointer; box-shadow: 4px 4px 0px var(--text-color); font-weight: bold; }
                                button.sendBtn:hover { transform: translate(2px, 2px); box-shadow: 2px 2px 0px var(--text-color); }
                            """
                        }
                    }
                }
                body {
                    div("game-container") {
                        h1 { span("pixel-icon") { +"👾" }
                            + " PIXEL CHAT "
                            span("pixel-icon") { +"👾" } }
                        div { id = "topBar"; div { id = "timerBox"; span("pixel-icon") { +"⏳" }; +"LOADING..." }; button { id = "hackBtn"; onClick = "extendTime()"; span("pixel-icon") { +"💖" }; +"LEVEL UP (+30s)" } }
                        div { id = "chatBox" }
                        div("input-area") { input { id = "msgInput"; type = InputType.text; placeholder = "INSERT COIN & TYPE..."; autoFocus = true; onKeyPress = "if(event.keyCode==13) sendMsg();" }; button { classes = setOf("sendBtn"); onClick = "sendMsg()"; +"START" } }
                    }
                    script {
                        unsafe {
                            +"""
                                var endTime = ${ChatService.boomTime.get()};
                                var myName = "";
                                setInterval(function() {
                                    var now = new Date().getTime(); var distance = endTime - now;
                                    if (distance < 0) { document.getElementById("timerBox").innerHTML = " GAME OVER"; document.getElementById("hackBtn").disabled = true; return; }
                                    var min = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60)); var sec = Math.floor((distance % (1000 * 60)) / 1000);
                                    document.getElementById("timerBox").innerHTML = '<span class="pixel-icon">⏳</span>TIME: ' + (min<10?'0'+min:min) + ":" + (sec<10?'0'+sec:sec);
                                    if(distance < 30000) document.getElementById("timerBox").style.color = "#ff0000";
                                }, 500);
                                var ws = new WebSocket((location.protocol === 'https:' ? 'wss:' : 'ws:') + "//" + location.host + "/chat");
                                ws.onmessage = function(e) {
                                    if (e.data.startsWith("MY_ID:")) { myName = e.data.split(":")[1]; return; }
                                    if (e.data.startsWith("SYNC_TIME:")) { endTime = parseInt(e.data.split(":")[1]); return; }
                                    var box = document.getElementById("chatBox"); var div = document.createElement("div"); div.classList.add("message-bubble");
                                    var msg = e.data;
                                    if (msg.startsWith("✨") || msg.startsWith("🎮") || msg.startsWith("💀") || msg.startsWith("🆙") || msg.startsWith("⚠")) div.classList.add("system-msg");
                                    else if (myName !== "" && msg.startsWith(myName + ":")) { div.classList.add("my-msg"); msg = msg.replace(myName + ": ", ""); }
                                    else div.classList.add("other-msg");
                                    div.innerText = msg; box.appendChild(div); box.scrollTop = box.scrollHeight;
                                };
                                function sendMsg() { var i = document.getElementById("msgInput"); if(i.value.trim()){ ws.send(i.value); i.value=""; } }
                                function extendTime() { ws.send("/extend"); var b=document.getElementById("hackBtn"); b.innerText="🆙 BOOSTING..."; setTimeout(function(){ b.innerHTML='<span class="pixel-icon">💖</span>LEVEL UP (+30s)';},1000); }
                            """
                        }
                    }
                }
            }
        }



    }
}