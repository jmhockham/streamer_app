# streamer_app
Example of handling hex/bit data packets. This is a simple webapp based on the [Play framework](https://www.playframework.com/). 

It is built in [Scala](https://www.scala-lang.org/download/); you can either import the project into IntelliJ (probably other IDE's as well, but I haven't checked), or if you've setup SBT, compile/run it from the project root using `sbt run`. It runs on port 9000, so you can check it's running by going to `localhost:9000`.

The endpoints can all be found in the `routes.conf` file. I've tried to make them as sensible as possible, but due to time constraints they're not very restful (everythings a get request). The endpoints are:
* GET `packets/handlePacket/{hex}`: processes a data packet
* GET `packets/history`: gets a dump of the session history (just a toString)
* GET `packets/resetSession`: resets/wipes the match data, starting a new gameplay session
* GET `packets/getMatchState`: gets the current match state

The endpoints can be queiried in a web browser (eg `localhost:9000/packets/history`) or via curl:
```bash 
curl -X GET "localhost:9000/packets/history"
```

## Code Explanation

I've put in some tests to do some simple good/bad parameters for the endpoints, but I haven't had the time to implement this with a swagger API (which would make it easy to query/test).

Regarding the inconsistent data: I decided that we were only going to use/display data if the match had "progressed" from the last data packet. Ie if the timer and the score for one team had gone up. If it's just the timer we don't really care, and if it's just the score then it's contradictory for a given time (or possibly erronious). There's an explanation on the `shouldUpdate` function in the `MatchState` class.

There should be some more testing around boundary data for the controller and the service, but I was running out of time. The tests present are enough to cover the fundamentals, but they could be expanded upon.
