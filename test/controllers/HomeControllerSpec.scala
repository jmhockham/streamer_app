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
  val sampleOne = Source.fromFile(getClass.getResource("/sample_one.txt").getFile)
  val sampleTwo = Source.fromFile(getClass.getResource("/sample_two.txt").getFile)

  "The controller" should "handle consistent data" in {
    val controller = getController
    for(line <- sampleOne.getLines.filterNot(_.isEmpty)){
      controller.doPacketHandling(line)
//      response shouldBe PacketData(line)
    }
    controller.sessionHistory.size shouldBe 28
  }

  it should "be able to reset the session history" in {
    val controller = getController
    controller.doPacketHandling(sampleOne.getLines.next())
    controller.sessionHistory.size shouldBe 1
    controller.handleResetSession
    controller.sessionHistory.isEmpty shouldBe true
  }

  private def getController: HomeController = {
    new HomeController(null, new StreamerService())
  }

  //TODO play fake requests, handling errors etc

}
