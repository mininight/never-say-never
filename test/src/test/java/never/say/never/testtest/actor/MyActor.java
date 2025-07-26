package never.say.never.testtest.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-10
 */
public class MyActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    System.out.println("Received message: " + message);
                })
                .match(Integer.class, message -> {
                    System.out.println("Received integer: " + message);
                })
                .match(CompletableFuture.class, future -> {
                    future.thenAcceptAsync(result -> {
                        getSender().tell(result, getSelf());
                    });
                })
                .build();
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ActorSystem system = ActorSystem.create("my-actor-system");
        // Create an instance of MyActor
        ActorRef myActor = system.actorOf(MyActor.props());
        // Send message to MyActor
        myActor.tell("Hello, Akka!", ActorRef.noSender());
        myActor.tell(123, ActorRef.noSender());
        // Use CompletableFuture to asynchronously get the result from MyActor
        CompletableFuture<String> future = new CompletableFuture<>();
        myActor.tell(future, ActorRef.noSender());
        // Wait for the future to complete and print the result
        String result = future.get();
        System.out.println("Received result: " + result);
        // Shutdown the actor system
        system.terminate();
    }
    public static Props props() {
        return Props.create(MyActor.class);
    }
}
