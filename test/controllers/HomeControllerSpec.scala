package controllers

import models.PacketData
import org.scalatest.{FlatSpec, Matchers}
import services.StreamerService

import scala.io.{Codec, Source}
import play.api.mvc.Results.Ok

import play.api.test._
import play.api.test.Helpers._

class HomeControllerSpec extends FlatSpec with Matchers {

  implicit val codec = Codec("UTF-8")

  "The controller" should "handle consistent data" in {
    val sampleOne = getSample("/sample_one.txt")
    val controller = getController
    for(line <- sampleOne.getLines.filterNot(_.isEmpty)){
      controller.doPacketHandling(line)
//      response shouldBe PacketData(line)
    }
    controller.sessionHistory.size shouldBe 28
  }

  it should "be able to reset the session history" in {
    val sampleOne = getSample("/sample_one.txt")
    val controller = getController
    controller.doPacketHandling(sampleOne.getLines.next())
    controller.sessionHistory.size shouldBe 1
    controller.handleResetSession
    controller.sessionHistory.isEmpty shouldBe true
  }

  private def getSample(filename: String) = {
    Source.fromFile(getClass.getResource(filename).getFile)
  }

  private def getController: HomeController = {
    new HomeController(null, new StreamerService())
  }

  it should "respond to the index Action" in new WithApplication {
    val controller = app.injector.instanceOf[controllers.HomeController]
    val result = controller.handlePacket("0x781002")(FakeRequest())

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

}
