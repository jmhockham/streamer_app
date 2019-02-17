package services

import java.lang

import models.PacketData
import play.api.Logger

class StreamerService {

  val packetMaxLength = 32 //indexing starts at 0

  def parsePacket(packet: String): PacketData = {
    val longTransformation = hexToLong(packet)
    val binaryTempStr = longToBinaryString(longTransformation)
    val binaryString = padIfNecessary(binaryTempStr)
    if(binaryString.length==packetMaxLength){
      val packetData = PacketData(binaryString)
      Logger.info(s"oringinal hex value $packet;\ninteger value $longTransformation;\nbinary value $binaryString;\n" + packetData.report)
      packetData
    }
    else{
      null
    }
  }

  private def longToBinaryString(longTransformation: lang.Long) = {
    java.lang.Long.toBinaryString(longTransformation)
  }

  private def hexToLong(x: String) = {
    java.lang.Long.decode(x)
  }

  private def padIfNecessary(bytes: String) = {
    if(bytes.length<packetMaxLength){
      bytes.reverse.padTo(packetMaxLength,'0').reverse
    }
    else{
      bytes
    }
  }

}