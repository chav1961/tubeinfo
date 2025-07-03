package chav1961.tubeinfo.utils;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelManager implements AutoCloseable{
	private static final int	NUMBER_OF_THREADS  = Runtime.getRuntime().availableProcessors();
	
	private final AtomicInteger	counter = new AtomicInteger();
	private final BlockingQueue<Future<Void>>	futures = new LinkedBlockingQueue<>(); 
	private ExecutorService		service;

	public ParallelManager() {
		this(NUMBER_OF_THREADS);
	}

	public ParallelManager(final int numberOfThreads) {
		if (numberOfThreads <= 0) {
			throw new IllegalArgumentException("Number of threads to process tasks ["+numberOfThreads+"] must be greater than 0");
		}
		else {
			this.service = Executors.newFixedThreadPool(numberOfThreads);
		}
	}
	
	@FunctionalInterface
	public static interface TaskInterface {
		void execute() throws Exception;
	}
	
	public void addTask(final TaskInterface ti) throws IOException {
		try {
			counter.incrementAndGet();
			futures.put(service.submit(()->{
				ti.execute();
				return null;
			}));
		} catch (InterruptedException e) {
			throw new IOException("Terminated");
		}
	}
	
	@Override
	public void close() throws IOException {
		while(counter.get() > 0) {
			try {
				futures.take().get(5, TimeUnit.SECONDS);
				counter.decrementAndGet();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException("Terminated");
			} catch (ExecutionException e) {
				throw new IOException(e.getCause());
			} catch (TimeoutException e) {
			}
		}
		try {
        	service.shutdown();
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Terminated");
		}
	} 
}