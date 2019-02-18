package controllers

import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject._

import models.PacketData
import play.api.Logger
import play.api.mvc._
import services.PacketService

import scala.collection.mutable

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, packetService: PacketService)
  extends AbstractController(cc) {


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  //this is public so that we can easily test it
  val sessionHistory: mutable.ListMap[String, PacketData] = new mutable.ListMap[String,PacketData]()

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def handlePacket(hex: String) = Action {
    if(hex!=null && hex.nonEmpty){
      try{
        Logger.info(s"Received packet $hex")
        val packetData = doPacketHandling(hex)
        Ok(s"${packetData.report}")
      }
      catch {
        case nfe: NumberFormatException => BadRequest(s"Must supply a hex: ${nfe.getLocalizedMessage}")
        case t: Throwable => BadRequest(t.getLocalizedMessage)
      }
    }
    else{
      BadRequest("no hex supplied")
    }
  }

  //public for testing
  def doPacketHandling(hex: String) = {
    val packetData = packetService.parsePacket(hex)
    sessionHistory.put(hex, packetData)
    packetData
  }

  def resetSession() = Action {
    handleResetSession
    Ok("Session cleared")
  }

  //public for testing
  def handleResetSession = {
    sessionHistory.clear()
  }

  def getSessionHistory() = Action {
    val history = sessionHistory.toSeq.sortBy(_._2.elapsedTime).map{ case (hex, packetData) =>
      packetData.report
    }.mkString("\n")
    val currentDatetime = getCurrentTime
    Ok(s"Session history at [$currentDatetime] :\n$history")
  }

  private def getCurrentTime: String = {
    val now = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
    minuteFormat.format(now)
  }

}
