package flocker

/**
 * Created by mglvl on 10/19/14.
 */
package object actor {
  type TopicActorRef = FlockerActorRef[TopicActor, TopicActor.TopicActorMessage]
  type ParsingActorRef = FlockerActorRef[ParsingActor, ParsingActor.ParsingActorMessage]
  type TrackingActorRef = FlockerActorRef[TrackingActor, TrackingActor.TrackingActorMessage]
  type TopicPersistentViewRef = FlockerActorRef[TopicPersistentView, TopicPersistentView.TopicPersistentViewMessage]
}
