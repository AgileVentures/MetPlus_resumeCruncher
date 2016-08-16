package org.metplus.curriculum.process;

import org.metplus.curriculum.cruncher.CrunchersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

/**
 * Created by joao on 3/25/16.
 */
public abstract class ProcessCruncher<Work> {
    private static final Logger logger = LoggerFactory.getLogger(ProcessCruncher.class);
    private Thread cruncher;
    private Deque<Work> workDeque;
    private boolean keepRunning = true;

    @Autowired
    protected CrunchersList allCrunchers;

    Semaphore semaphore;

    /**
     * Post constructor function
     * The function will initialize the workDeque and start
     * the thread that will crunch the workDeque
     */
    @PostConstruct
    public void postConstructor() {
        semaphore = new Semaphore(1);
        workDeque = new ArrayDeque<>();
        start();
    }

    /**
     * Main function that will be always running and crunching the workDeque
     */
    public void run() {
        logger.info("Processor started");
        do {
            logger.debug("Going to check workDeque");
            Work work = null;
            while ((work = nextWork()) != null) {
                logger.debug("Going to process work");
                process(work);
            }
            logger.debug("No more work to process, sleep waiting");
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(keepRunning);

        logger.warn("Processor stopped");
    }

    /**
     * Method that process the work in the queue
     * @param work Work to be processed
     */
    protected abstract void process(Work work);

    /**
     * Stop the thread
     */
    public synchronized void stop(){
        logger.warn("Requested stop of processor");
        setKeepRunning(false);
        semaphore.release();
    }

    /**
     * Change the control variable of the thread
     * @param keepRunning True if the thread should keep on running, false otherwise
     */
    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    /**
     * Function to start the thread
     */
    public void start() {
        cruncher = new Thread(){
            public void run() {
                ProcessCruncher.this.run();
            }
        };
        cruncher.start();
    }

    /**
     * Add a new work to be crunched by the thread
     * @param work Resume to be added
     */
    public synchronized void addWork(Work work) {
        workDeque.add(work);
        semaphore.release();
    }

    /**
     * Retrieve the next work object to be crunched
     * @return Work Work object or null if there is no work
     */
    protected synchronized Work nextWork() {
        try {
            return workDeque.remove();
        } catch(NoSuchElementException exp) {
            return null;
        }
    }

    /**
     * Method waits for the thread to stop
     * Warning: This method do not perform a Stop on the thread.
     *          Call method stop() before calling this method
     * @throws InterruptedException When the thread is interrupted
     */
    public void join() throws InterruptedException {
        cruncher.join();
    }
}
