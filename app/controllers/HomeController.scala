package controllers

import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject._

import models.PacketData
import play.api.Logger
import play.api.mvc._
import services.StreamerService

import scala.collection.mutable

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, streamerService: StreamerService)
  extends AbstractController(cc) {


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  //this is public so that we can easily test it
  val sessionHistory: mutable.HashMap[String, PacketData] = new mutable.HashMap[String,PacketData]()

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def handlePacket(hex: String) = Action {
    if(hex!=null && hex.nonEmpty){
      Logger.info(s"Received packet $hex")
      val packetData = doPacketHandling(hex)
      Ok(s"${packetData.report}")
    }
    else{
      BadRequest("no hex supplied")
    }
  }

  //public for testing
  def doPacketHandling(hex: String) = {
    val packetData = streamerService.parsePacket(hex)
    sessionHistory.put(hex, packetData)
    packetData
  }

  def resetSession() = Action {
    sessionHistory.clear()
    Ok("Session cleared")
  }

  def getSessionHistory() = Action {
    val history = sessionHistory.map{ case (hex, packetData) =>
      packetData.report
    }
    val currentDatetime = getCurrentTime
    Ok(s"Session history at [$currentDatetime] :\n$history")
  }

  private def getCurrentTime: String = {
    val now = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
    minuteFormat.format(now)
  }

}
