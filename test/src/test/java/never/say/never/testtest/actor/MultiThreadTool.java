package never.say.never.testtest.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-10
 */
public class MultiThreadTool {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ActorSystem system = ActorSystem.create("multi-thread-system");
        // Create an instance of WorkerActor with RoundRobinPool router
        ActorRef workerActor = system.actorOf(
                new RoundRobinPool(5).props(WorkerActor.props()), "worker-actor"
        );
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            CompletableFuture<String> future = new CompletableFuture<>();
            futures.add(future);
            // Send task to WorkerActor
            workerActor.tell(new Task(i, future), ActorRef.noSender());
        }
        // Wait for all tasks to complete
        List<String> results = new ArrayList<>();
        for (CompletableFuture<String> future : futures) {
            results.add(future.get());
        }
        System.out.println("Results: " + results);
        system.terminate();
    }
    private static class Task {
        private final int taskId;
        private final CompletableFuture<String> future;
        public Task(int taskId, CompletableFuture<String> future) {
            this.taskId = taskId;
            this.future = future;
        }
        public int getTaskId() {
            return taskId;
        }
        public CompletableFuture<String> getFuture() {
            return future;
        }
    }
    private static class WorkerActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Task.class, message -> {
                        System.out.println("WorkerActor received task " + message.getTaskId());
                        // Do some heavy computation
                        Thread.sleep(1000);
                        String result = "Task " + message.getTaskId() + " completed.";
                        // Send result back to main thread
                        message.getFuture().complete(result);
                    })
                    .build();
        }
        public static Props props() {
            return Props.create(WorkerActor.class);
        }
    }
}
