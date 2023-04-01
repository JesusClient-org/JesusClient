package cum.jesus.jesusclient.utils.slaves;

import cum.jesus.jesusclient.utils.Logger;

import java.util.concurrent.*;

public class WorkerPool {
    private int workerNum;
    private ExecutorService executor;

    public WorkerPool(int workerNum) {
        this.workerNum = workerNum;
        this.executor = Executors.newFixedThreadPool(workerNum);
    }

    public CompletableFuture<Void> queueJob(Job job) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                job.run();
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    public void kill() {
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // Handle exception
        }
    }
}
