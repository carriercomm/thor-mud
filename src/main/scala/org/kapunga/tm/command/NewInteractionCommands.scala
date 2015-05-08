package org.kapunga.tm.command

import org.kapunga.tm.command.CommandHelpers._
import org.kapunga.tm.world.Universe

/**
 * Created by kapunga on 5/8/15.
 */
object NewInteractionCommands extends NewCommandRegistry {
  val look = Root("look", Nil, makeHelp("look"), new ExecFunction((context, subCommand) => context.room.look(context.executor)))

  val pantheons = Root("pantheons", Nil, makeHelp("pantheons"), new ExecFunction((context, subCommand) => {
    Universe.allPantheons.foreach(p => context.executor.tell(p.verboseDesc))
    context.executor.prompt()
  }))

  val commandList = look :: pantheons :: Nil

  override def registerCommands(register: Root => Unit) = commandList.foreach(x => register(x))
}