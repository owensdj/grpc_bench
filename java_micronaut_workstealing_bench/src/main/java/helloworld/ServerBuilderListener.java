package helloworld;

import io.grpc.ServerBuilder;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;

import javax.inject.Singleton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.concurrent.Executors;

@Singleton
public class ServerBuilderListener implements BeanCreatedEventListener<ServerBuilder<?>> {
    @Override
    public ServerBuilder<?> onCreated(BeanCreatedEvent<ServerBuilder<?>> event) {
        final ServerBuilder<?> builder = event.getBean();
        builder.maxInboundMessageSize(1024);
        configureExecutor(builder);
        return builder;
    }

    /**
      * Allow customization of the Executor with two environment variables:
       * 
       * <p>
       * <ul>
       * <li>JVM_EXECUTOR_TYPE: direct, workStealing, single, fixed, cached</li>
       * <li>JVM_EXECUTOR_THREADS: integer value.</li>
       * </ul>
       * </p>
       * 
       * The number of Executor Threads will default to the number of
       * availableProcessors(). Only the workStealing and fixed executors will use
       * this value.
       */
      private ServerBuilder<?> configureExecutor(ServerBuilder<?> sb) {
        var threads = System.getenv("JVM_EXECUTOR_THREADS");
        var i_threads = Runtime.getRuntime().availableProcessors();
        if (threads != null && !threads.isEmpty()) {
          i_threads = Integer.parseInt(threads);
        }
    
        var value = System.getenv().getOrDefault("JVM_EXECUTOR_TYPE", "workStealing");
        switch (value) {
          case "direct" -> sb = sb.directExecutor();
          case "single" -> sb = sb.executor(Executors.newSingleThreadExecutor());
          case "fixed" -> sb = sb.executor(Executors.newFixedThreadPool(i_threads));
          case "workStealing" -> sb = sb.executor(Executors.newWorkStealingPool(i_threads));
          case "cached" -> sb = sb.executor(Executors.newCachedThreadPool());
        }
    
        return sb;
      }
    
}
