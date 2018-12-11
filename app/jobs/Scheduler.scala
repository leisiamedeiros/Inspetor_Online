package jobs

import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import akka.actor.{ ActorRef, ActorSystem, actorRef2Scala }

class Scheduler @Inject() (
  system: ActorSystem,
  @Named("auth-token-cleaner") authTokenCleaner: ActorRef) {
  QuartzSchedulerExtension(system).schedule(
    "AuthTokenCleaner", authTokenCleaner, AuthTokenCleaner.Clean)

  authTokenCleaner ! AuthTokenCleaner.Clean
}
