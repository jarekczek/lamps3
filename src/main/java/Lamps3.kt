import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiPin
import java.util.*

object Lamps3 {
  val gpio = GpioFactory.getInstance()
  val rnd = Random()

  val allLamps = listOf(
    LampDevice(gpio, RaspiPin.GPIO_27!!, RaspiPin.GPIO_23!!),
    LampDevice(gpio, RaspiPin.GPIO_28!!, RaspiPin.GPIO_24!!),
    LampDevice(gpio, RaspiPin.GPIO_29!!, RaspiPin.GPIO_25!!)
  )

  fun play(lampCount: Int) {
    println("wait")
    val lamps = allLamps.take(lampCount)
    while (allLamps.all { !it.isPressed() })
      blink(allLamps)
    while (allLamps.any { it.isPressed() })
    while (true) {
      singleGame(lamps, 10)
    }
  }

  private fun singleGame(lamps: List<LampDevice>, maxHits: Int) {
    blink(lamps)
    Thread.sleep(1500 + rnd.nextInt(1000).toLong())
    println("go")
    var lastLamp: LampDevice? = null
    val times = mutableListOf<Int>()
    var penalties = 0
    while (times.size < maxHits) {
      val possibleLamps = lamps.filter { it != lastLamp }
      val lamp = randomElement(possibleLamps, rnd)
      lamp.output.high()
      val t0 = System.currentTimeMillis()
      val badLamps = mutableSetOf<LampDevice>()
      while (lamp.input.isHigh) {
        val badLampsBefore = badLamps.size
        badLamps.addAll(lamps.filter { it != lamp && it != lastLamp && it.input.isLow })
        if (badLamps.size > badLampsBefore)
          print("penalty ")
        Thread.sleep(1)
      }
      while (lamp.input.isLow) {
        Thread.sleep(1)
      }
      penalties += badLamps.size
      lamp.output.low()
      val t = System.currentTimeMillis() - t0
      times.add(t.toInt())
      print(t.toString() + " ")
      lastLamp = lamp
    }
    println()
    printStats(times, penalties)
    Thread.sleep(2000)
    blink(allLamps)
    while (lamps.all { !it.isPressed() }) {}
    while (lamps.any { it.isPressed() }) {}
  }

  private fun printStats(times: List<Int>, penalties: Int) {
    val avg = times.average()
    val stdDev = Math.sqrt(times.map { (it - avg) * (it - avg) }.sum() / (times.size - 1))
    print("count: " + times.size + ", avg: " + times.average() +
            ", std dev: " + stdDev + ", min: " + times.min() + ", max: " + times.max())
    if (penalties > 0)
      print(", penalties: " + penalties)
    println()
  }

  private fun blink(lamps: List<LampDevice>) {
    lamps.forEach {
      it.output.high()
      Thread.sleep(100)
      it.output.low()
      Thread.sleep(100)
    }
  }

  private fun blinkFor(lamps: List<LampDevice>, millis: Int) {
    val t0 = System.currentTimeMillis()
    while (System.currentTimeMillis() - t0 < millis)
      blink(lamps)
  }

  private fun <T> randomElement(elements: List<T>, rnd: Random): T {
    return elements[rnd.nextInt(elements.size)]
  }

  @JvmStatic fun main(args: Array<String>) {
    Lamps3.play(args[0].toInt())
  }
}