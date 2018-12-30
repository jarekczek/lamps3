import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.GpioPinDigitalOutput
import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.PinMode
import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.RaspiPin

class LampDevice(gpio: GpioController, inputPin: Pin,
                 val outputPin: Pin)
{
  val input = gpio.provisionDigitalInputPin(inputPin, PinPullResistance.PULL_UP)
  val output = gpio.provisionDigitalOutputPin(outputPin)

  init {
    output.setShutdownOptions(false, PinState.LOW, null, PinMode.DIGITAL_OUTPUT)
  }

  fun isPressed() = input.isLow
}