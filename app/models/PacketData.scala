package models

case class PacketData (byteString: String) {

  private val pointsBits = byteString.reverse.substring(0,2).reverse
  private val teamIdBits = byteString.reverse.substring(2,3).reverse
  private val teamTwoPointsTotalBits = byteString.reverse.substring(3,11).reverse
  private val teamOnePointsTotalBits = byteString.reverse.substring(11,19).reverse
  private val elapsedTimeBits = byteString.reverse.substring(19,31).reverse

  def pointsScored: Int = {
    pointsBits match {
      case "00" => 1
      case "10" => 2
      case "01" => 2
      case "11" => 3
      case _    => 0
    }
  }

  def whoScored: String = {
    teamIdBits match {
      case "0" => "Team One"
      case "1" => "Team Two"
      case _   => "N/A"
    }
  }

  def teamOneTotal: Int = {
    java.lang.Integer.parseUnsignedInt(teamOnePointsTotalBits, 2)
  }

  def teamTwoTotal: Int = {
    java.lang.Integer.parseUnsignedInt(teamTwoPointsTotalBits,2)
  }

  def elapsedTime = {
    java.lang.Integer.parseUnsignedInt(elapsedTimeBits,2)
  }

  def report = {
    val dataReport = s"Packet Data: \n  " +
      s"points scored $pointsScored;\n  " +
      s"who scored $whoScored;\n  " +
      s"team one points $teamOneTotal\n  " +
      s"team two points $teamTwoTotal\n  " +
      s"elapsed time (seconds) $elapsedTime\n  "
    dataReport
  }

}

