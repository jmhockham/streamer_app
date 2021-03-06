package controllers

import org.scalatest.{FlatSpec, Matchers}
import services.PacketService
import scala.io.{Codec, Source}
import play.api.test._
import play.api.test.Helpers._

class HomeControllerSpec extends FlatSpec with Matchers {

  implicit val codec: Codec = Codec("UTF-8")

  "session history" should "handle consistent data" in {
    val sampleOne = getSample("/sample_one.txt")
    val controller = getController
    for(line <- sampleOne.getLines.filterNot(_.isEmpty)){
      controller.doPacketHandling(line)
    }
    controller.sessionHistory.size shouldBe 28
    //check an arbitrary hex
    controller.sessionHistory.get("0x3b8384b").nonEmpty shouldBe true
    controller.sessionHistory.get("0x3b8384b").head.pointsScored shouldBe 3
  }

  it should "be able to be reset" in {
    val sampleOne = getSample("/sample_one.txt")
    val controller = getController
    controller.doPacketHandling(sampleOne.getLines.next())
    controller.sessionHistory.size shouldBe 1
    controller.handleResetSession()
    controller.sessionHistory.isEmpty shouldBe true
  }

  it should "process inconsistent data" in {
    val sampleOne = getSample("/sample_two.txt")
    val controller = getController
    for(line <- sampleOne.getLines.filterNot(_.isEmpty)){
      controller.doPacketHandling(line)
    }
    controller.sessionHistory.size shouldBe 29
  }

  private def getSample(filename: String) = {
    Source.fromFile(getClass.getResource(filename).getFile)
  }

  private def getController: HomeController = {
    new HomeController(null, new PacketService())
  }

  "handlePacket" should "handle legitimate hex packets correctly" in new WithApplication {
    private val controller = app.injector.instanceOf[controllers.HomeController]
    private val result = controller.handlePacket("0x781002")(FakeRequest())

    status(result) shouldBe OK
    contentType(result) shouldBe Some("text/plain")
    contentAsString(result) should (
      include("points scored 2") and
      include("who scored Team One") and
      include("team one points 2") and
      include("team two points 0") and
      include("elapsed time (seconds) 15")
    )
  }

  it should "throw a bad response if nothing is supplied" in new WithApplication {
    private val controller = app.injector.instanceOf[controllers.HomeController]
    private val result = controller.handlePacket("")(FakeRequest())

    status(result) shouldBe BAD_REQUEST
  }

  it should "throw a bad response if a non-hex value is supplied" in new WithApplication {
    private val controller = app.injector.instanceOf[controllers.HomeController]
    private val result = controller.handlePacket("abc123")(FakeRequest())

    status(result) shouldBe BAD_REQUEST
  }

  "getMatchState" should "correctly parse a packet" in new WithApplication {
    private val controller = app.injector.instanceOf[controllers.HomeController]
    controller.handlePacket("0xe01016")(FakeRequest())
    private val result = controller.getMatchState(FakeRequest())

    status(result) shouldBe OK
    contentType(result) shouldBe Some("text/plain")
    contentAsString(result) should include ("Team One vs Team Two: 2-2 (0:28; Team Two, 2)")
  }

  it should "only update if the match has progressed" in new WithApplication {
    private val controller = app.injector.instanceOf[controllers.HomeController]
    controller.handlePacket("0xe01016")(FakeRequest())
    controller.handlePacket("0x1081014")(FakeRequest())
    private val result = controller.getMatchState(FakeRequest())

    status(result) shouldBe OK
    contentType(result) shouldBe Some("text/plain")
    contentAsString(result) should include ("Team One vs Team Two: 2-2 (0:28; Team Two, 2)")
  }

}
