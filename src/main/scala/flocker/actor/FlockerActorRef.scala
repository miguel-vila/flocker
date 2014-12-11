package flocker.actor

import akka.actor.{ActorRef, Actor}

/**
 * Un wrapper de ActorRef para conservar un poco de "typesafety" y dar mas legibilidad al código
 * Las instancias se encuentran en el package object
 * Las funciones 'actorRef' de cada Actor 'X' deben devolver un 'FlockerActorRef[X]'
 * Este 'wrapping' no tiene consecuencias en tiempo de ejecución: http://docs.scala-lang.org/overviews/core/value-classes.html
 */
case class FlockerActorRef[A <: Actor, M](ref: ActorRef) extends AnyVal {
  def !(message: M)(implicit sender: ActorRef = Actor.noSender): Unit = {
    ref ! message
  }
}