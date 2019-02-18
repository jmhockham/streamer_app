package services

import org.scalatest.{FlatSpec, Matchers}

class PacketServiceSpec extends FlatSpec with Matchers {

  val streamerService = new PacketService

  "parsePacket" should "correctly parse normal packets" in {
    //15s T1 +2pt 2:0
    //"0x781002"
    val packetDataOne = streamerService.parsePacket("0x781002")
    packetDataOne.elapsedTime shouldBe 15
    packetDataOne.whoScored shouldBe "Team One"
    packetDataOne.teamOneTotal shouldBe 2
    packetDataOne.teamTwoTotal shouldBe 0
    packetDataOne.pointsScored shouldBe 2

    //30s T2 +3pt 2:3
    //"0xf0101f"
    val packetDataTwo = streamerService.parsePacket("0xf0101f")
    packetDataTwo.elapsedTime shouldBe 30
    packetDataTwo.whoScored shouldBe "Team Two"
    packetDataTwo.teamOneTotal shouldBe 2
    packetDataTwo.teamTwoTotal shouldBe 3
    packetDataTwo.pointsScored shouldBe 3

    //10:10 T1 +1pt 25:20
    //"0x1310c8a1"
    val packetDataThree = streamerService.parsePacket("0x1310c8a1")
    packetDataThree.elapsedTime shouldBe 610
    packetDataThree.whoScored shouldBe "Team One"
    packetDataThree.teamOneTotal shouldBe 25
    packetDataThree.teamTwoTotal shouldBe 20
    packetDataThree.pointsScored shouldBe 1
  }

}
