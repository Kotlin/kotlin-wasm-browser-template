import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import kotlin.test.Test
import kotlin.test.assertEquals

class BrowserTest {
    @Test
    fun mainTest() {
        val mockContainer = document.createElement("div")
        mockContainer.addTimeZoneWidget()
        assertEquals("Time in  is ", mockContainer.textContent)

        val inputs = mockContainer.getElementsByTagName("input")

        assertEquals(1, inputs.length)

        val timezoneInput = inputs[0]!!.unsafeCast<HTMLInputElement>()
        assertEquals("text", timezoneInput.type)
        assertEquals("Timezone", timezoneInput.placeholder)
        assertEquals("Europe/Amsterdam", timezoneInput.value)

        val outputs = mockContainer.getElementsByTagName("span")

        assertEquals(1, outputs.length)
    }
}