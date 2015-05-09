package org.kapunga.tm

import akka.actor.{Props, ActorSystem}
import akka.routing.{RoundRobinPool, DefaultResizer}
import org.kapunga.tm.command.{CommandExecutor, CommandExecutorService}
import org.kapunga.tm.world.Universe
import org.slf4j.LoggerFactory

/**
 * The top level component to the ThorMUD system.  Is responsible for starting and stopping
 * the actor system and starting up core actors.
 *
 * @author Paul J Thordarson kapunga@gmail.com
 */
class ThorMud {
  implicit val system = ActorSystem(ThorMud.ACTOR_SYSTEM_NAME)
  val log = LoggerFactory.getLogger("ThorMUD-root")

  def start() = {
    log.info("Starting up NewCommandExecutorService...")
    val newPoolResizer = Some(DefaultResizer(lowerBound = 2, upperBound = 15))
    val newPool = system.actorOf(RoundRobinPool(5, newPoolResizer).props(Props[CommandExecutor]), "newCommandRouter")
    if (CommandExecutorService.initExecutorPool(newPool)) {
      log.info("NewCommandExecutorService started.")
    } else {
      log.error("Unable to start NewCommandExecutorService, shutting down.")
      system.shutdown()
    }

    log.info("Initializing universe...")
    Universe.init()
    log.info("Universe initialized.")

    // Start up the TCP server
    if (!system.isTerminated) system.actorOf(TcpServer.serverProps(12345), ThorMud.TCP_ACTOR_NAME)
  }

  def stop() = {
    system.shutdown()
  }
}

/**
 * The companion Object to the ThorMud class.  Primarily contains global constants
 * to things like the actor system name and top level actor names.
 * @author Paul J Thordarson kapunga@gmail.com
 */
object ThorMud {
  val ACTOR_SYSTEM_NAME = "thorMudSystem"
  val TCP_ACTOR_NAME = "tcpServer"
}
