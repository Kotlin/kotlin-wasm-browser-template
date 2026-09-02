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

    window.fetch("https://timeapi.io/api/Time/current/zone?timeZone=${input.value}")
        .then { response ->
            window.clearInterval(progressId)

            if (response.ok) {
                response.json().then { json ->
                    val timeApiResponse = json?.unsafeCast<TimeApiResponse>()
                    val date = timeApiResponse?.date ?: "🤔"
                    val time = timeApiResponse?.time ?: "🧐"
                    output.textContent = "📅 $date ⏰ $time" 
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

/*
Response for `https://timeapi.io/api/Time/current/zone?timeZone=Europe/Amsterdam`:
```json 
{
  "year": 2026,
  "month": 3,
  "day": 18,
  "hour": 14,
  "minute": 2,
  "seconds": 26,
  "milliSeconds": 382,
  "dateTime": "2026-03-18T14:02:26.3823348",
  "date": "03/18/2026",
  "time": "14:02",
  "timeZone": "Europe/Amsterdam",
  "dayOfWeek": "Wednesday",
  "dstActive": false
}
```
 */
external interface TimeApiResponse: JsAny {
    val year: Int
    val month: Int
    val day: Int
    val hour: Int
    val minute: Int
    val seconds: Int
    val milliSeconds: Int
    val dateTime: String
    val date: String
    val time: String
    val timeZone: String
    val dayOfWeek: String
    val dstActive: Boolean

}
