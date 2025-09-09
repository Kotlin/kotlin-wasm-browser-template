@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.appendElement
import kotlinx.dom.appendText
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement

fun Element.addTimeZoneWidget() {
    appendElement("div") {
        appendText("Time in ")

        val output = document.createElement("span")

        val input = appendElement("input") {
            this as HTMLInputElement
            type = "text"
            placeholder="Timezone"

            value = "Europe/Amsterdam"

            addEventListener("change") {
                updateTime(this, output)
            }
        } as HTMLInputElement

        appendText(" is ")
        appendChild(output)

        updateTime(input, output)
    }
}

private val progress = "⡆⠇⠋⠙⠸⢰⣠⣄".map(Char::toString)

private fun updateTime(input: HTMLInputElement, output: Element) {
    var i = 0
    val progressId = window.setInterval({
        output.textContent = progress[i]
        i = (i + 1) % progress.size
        null
    }, 100)

    window.fetch("https://worldtimeapi.org/api/timezone/${input.value}")
        .then { response ->
            window.clearInterval(progressId)

            if (response.ok) {
                response.json().then { json ->
                    output.textContent = json?.unsafeCast<WorldTimeApiResponse>()?.datetime
                        ?.substringAfter("T")?.substringBefore(".") ?: "🧐"
                    null
                }
            } else {
                output.textContent = "🤷 " + response.status
            }
            null
        }
        .catch {
            window.clearInterval(progressId)
            output.textContent = "🙅🛜"
            null
        }
}

external interface WorldTimeApiResponse: JsAny {
    val datetime: String?
}
