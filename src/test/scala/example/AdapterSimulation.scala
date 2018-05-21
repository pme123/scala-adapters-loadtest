package example

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AdapterSimulation extends Simulation {

  val scn: ScenarioBuilder =
    scenario("WebSocket")
      .exec(Seq(RestServices.jobConfigs
        , RestServices.clientConfigs)
        ++ Webpage.jobProcess
        ++ Webpage.jobResults
        ++ Webpage.customPage
        ++ Websocket.runAdapters)

  setUp(
    scn.inject(rampUsers(500) over 60.seconds)
  ).protocols(Config.httpConf)
}

object Config {
  private val baseUrl = "http://localhost:9000"
  private val wsBaseUrl = "ws://localhost:9000"
  private val authUsername = "admin"
  private val authPassword = "doesnotmatter"

  val jobs = Seq("demoJob", "demoJobWithDefaultScheduler", "demoJobWithoutScheduler")

  val httpConf: HttpProtocolBuilder = http
    .baseURL(baseUrl)
    .basicAuth(authUsername, authPassword)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling2")
    .wsBaseURL(wsBaseUrl)

}

object RestServices {
  val jobConfigs: ChainBuilder =
    exec(http("JobConfigs")
      .get("/jobConfigs"))
      .pause(2)

  val clientConfigs: ChainBuilder =
    exec(http("ClientConfigs")
      .get("/clientConfigs"))
      .pause(2)

}

object Websocket {
  val runAdapters: Seq[ChainBuilder] =
    Config.jobs.map(job =>
      exec(ws(s"Connect WS $job")
        .open(s"/ws/$job")
        .check(wsListen.within(3.seconds).until(4)))
        .pause(1)
        .exec(ws(s"Run $job")
          .sendText("""{"RunJob":{"userName": "LoadTester"}}""")
          .check(wsListen.within(2.seconds).until(1)))
        .pause(10)
        .exec(ws("Close WS").close)
    )
}

object Webpage {
  val jobProcess: Seq[ChainBuilder] =
    Config.jobs.map(job =>
      exec(http(s"JobProcess: $job")
        .get(s"/jobProcess/$job"))
        .pause(1))
  val jobResults: Seq[ChainBuilder] =
    Config.jobs.map(job =>
      exec(http(s"JobResults: $job")
        .get(s"/jobResults/$job"))
        .pause(1))
  val customPage: Seq[ChainBuilder] =
    Config.jobs.map(job =>
      exec(http(s"CustomPage: $job")
        .get(s"/customPage/$job"))
        .pause(1))
}