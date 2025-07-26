package never.say.never.testtest.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestPool {
    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load("application.properties");
        final ActorSystem system = ActorSystem.create("AkkaThreadPoolExample",config);
        // Create a pool of 10 actors to process tasks
        final ActorRef actorPool = system.actorOf(
                Props.create(MyActor.class)
                        .withRouter(new RoundRobinPool(10)),
                "actorPool");
        // Submit 100 tasks to the actor pool
        for (int i = 0; i < 100; i++) {
            actorPool.tell(new MyTask(i), ActorRef.noSender());
        }
        // Wait for the tasks to complete
        system.stop(actorPool);
        system.terminate();
    }
    // Define the task to be processed
    private static class MyTask {
        private final int taskId;
        public MyTask(int taskId) {
            this.taskId = taskId;
        }
    }
    // Define the actor that processes tasks
    private static class MyActor extends akka.actor.UntypedAbstractActor {
        public void onReceive(Object message) throws Throwable {
            // Process the task
            if (message instanceof MyTask) {
                MyTask task = (MyTask) message;
                System.out.println("Processing task: " + task.taskId);
                // ...
            }
        }
    }
}