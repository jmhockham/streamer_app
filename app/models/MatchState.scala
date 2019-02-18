package models

import java.text.SimpleDateFormat

case class MatchState (
  teamOneName: String = "Team One",
  teamTwoName: String = "Team Two",
  elapsedTime: Int,
  teamOneScore: Int,
  teamTwoScore: Int,
  whoJustScored: String,
  lastPointScored: Int
) {

  def report: String = {
    s"$teamOneName vs $teamTwoName: $teamOneScore-$teamTwoScore (${formatTime(elapsedTime)}; $whoJustScored, $lastPointScored)"
  }
  private def formatTime(seconds: Int) = {
    val min = seconds / 60
    val sec = seconds % 60
    s"$min:$sec"
  }

  /**
    * Determines whether or not the match score should be updated. Bearing in mind that we can't have points deducted,
    * we only update the status if the match has "progressed" by some reasonable margin.
    *
    * We almost certainly don't care about data that's in the past (and for the sake of simplicity we're going
    * to assume the elapsed time isn't always wildly inaccurate), but that means nothing if the score didn't
    * change, so we check that as well. As we're only interested in the match progressing, we only update if
    * the time AND a score has gone up. Otherwise why would we care?
    *
    * @param packetData the packet we're checking against
    * @return true if we should update, based on the current values
    */
  def shouldUpdate(packetData: PacketData): Boolean = {
    if(packetData.elapsedTime>elapsedTime &&
      (packetData.teamTwoTotal>teamTwoScore || packetData.teamOneTotal>teamOneScore)){
      return true
    }
    false
  }

}

object MatchState {
  def formPacketData(packetData: PacketData) = {
    MatchState(
      elapsedTime = packetData.elapsedTime,
      teamOneScore = packetData.teamTwoTotal,
      teamTwoScore = packetData.teamTwoTotal,
      whoJustScored = packetData.whoScored,
      lastPointScored = packetData.pointsScored
    )
  }

}
