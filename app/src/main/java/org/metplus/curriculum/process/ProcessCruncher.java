package org.metplus.curriculum.process;

import org.metplus.curriculum.cruncher.CrunchersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    CountDownLatch jobSignal;

    /**
     * Post constructor function
     * The function will initialize the workDeque and start
     * the thread that will crunch the workDeque
     */
    @PostConstruct
    public void postConstructor() {
        jobSignal = new CountDownLatch(1);
        workDeque = new ArrayDeque<>();
        start();
    }

    /**
     * Main function that will be always running and crunching the workDeque
     */
    public void run() {
        do {
            logger.debug("Going to check workDeque");
            Work work = null;
            while ((work = nextWork()) != null) {
                logger.debug("Going to process work");
                process(work);
            }
            try {
                jobSignal.await(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(keepRunning);
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
        setKeepRunning(false);
        jobSignal.countDown();
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
        jobSignal.countDown();
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
}
