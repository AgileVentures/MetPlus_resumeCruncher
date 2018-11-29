package org.metplus.cruncher.rating

import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Semaphore

abstract class ProcessCruncher<Work>(
        private val allCrunchers: CruncherList
) {
    private val logger = LoggerFactory.getLogger(ProcessCruncher::class.java)
    private var cruncher: Thread? = null
    private var workDeque: Deque<Work> = ArrayDeque()
    private var keepRunning = true

    private var semaphore: Semaphore = Semaphore(1)

    fun run() {
        logger.info("Processor started")
        do {
            logger.debug("Going to check workDeque")
            var work: Work? = nextWork()
            while (work != null) {
                logger.debug("Going to process work")
                try {
                    process(work)
                } catch (exp: Exception) {
                    logger.error("Unhandled exception on work: $work:$exp")
                }
                work = nextWork()
            }
            logger.debug("No more work to process, sleep waiting")
            try {
                semaphore.acquire()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        } while (keepRunning)

        logger.warn("Processor stopped")
    }

    protected abstract fun process(work: Work)

    @Synchronized
    fun stop() {
        logger.warn("Requested stop of processor")
        setKeepRunning(false)
        semaphore.release()
    }

    fun setKeepRunning(keepRunning: Boolean) {
        this.keepRunning = keepRunning
    }

    fun start() {
        cruncher = object : Thread() {
            override fun run() {
                this@ProcessCruncher.run()
            }
        }
        cruncher!!.start()
    }

    @Synchronized
    fun addWork(work: Work) {
        workDeque.add(work)
        semaphore.release()
    }

    @Synchronized
    protected fun nextWork(): Work? {
        return try {
            workDeque.remove()
        } catch (exp: NoSuchElementException) {
            null
        }

    }

    @Throws(InterruptedException::class)
    fun join() {
        cruncher!!.join()
    }
}
