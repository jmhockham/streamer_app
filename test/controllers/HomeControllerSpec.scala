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
  val sampleOne = Source.fromFile(getClass.getResource("/sample_one.txt").getFile).getLines
  val sampleTwo = Source.fromFile(getClass.getResource("/sample_two.txt").getFile).getLines

  "The controller" should "handle consistent data" in {
    val controller = new HomeController(null, new StreamerService())
    for(line <- sampleOne.filterNot(_.isEmpty)){
      controller.doPacketHandling(line)
//      response shouldBe PacketData(line)
    }
    controller.sessionHistory.size shouldBe 28
  }

}
