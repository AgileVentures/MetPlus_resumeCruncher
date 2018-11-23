package org.metplus.cruncher.rating

import org.junit.jupiter.api.Test
import org.metplus.cruncher.web.SpringMongoConfig
import org.mockito.Mockito
import java.io.ByteArrayOutputStream
import java.util.*

internal class CrunchResumeProcessTest {
    class Run {
        private val allCrunchers: CrunchersList? = null
        private val springMongoConfig: SpringMongoConfig? = null
        private val resumeRepository: ResumeRepository? = null
        private val cruncher: ResumeCruncher? = null
        private val cruncherImpl: Cruncher? = null

        @Test
        @Throws(InterruptedException::class)
        fun noResumes() {
            cruncher!!.postConstructor()
            cruncher!!.stop()
            cruncher!!.join()
            Mockito.verify<Any>(allCrunchers, Mockito.times(0)).getCrunchers()
            Mockito.verify<Any>(resumeRepository, Mockito.times(0)).save(Mockito.any(Resume::class.java!!))
        }

        @Test
        @Throws(ResumeNotFound::class, ResumeReadException::class, InterruptedException::class)
        fun oneResume() {
            val resume = Mockito.mock(Resume::class.java)
            Mockito.`when`(resume.getResume(springMongoConfig)).thenReturn(ByteArrayOutputStream())
            val listCrunchers = ArrayList<Cruncher>()
            listCrunchers.add(cruncherImpl)
            Mockito.`when`(allCrunchers!!.getCrunchers()).thenReturn(listCrunchers)
            cruncher!!.postConstructor()
            cruncher!!.addWork(resume)
            cruncher!!.stop()
            cruncher!!.join()
            Mockito.verify<Any>(allCrunchers, Mockito.times(1)).getCrunchers()
            Mockito.verify<Any>(resumeRepository, Mockito.times(1)).save(resume)
            Mockito.verify<Any>(resume).getResume(springMongoConfig)
        }

        @Test
        @Throws(ResumeNotFound::class, ResumeReadException::class, InterruptedException::class)
        fun twoResume() {
            val resume = Mockito.mock(Resume::class.java)
            Mockito.`when`(resume.getResume(springMongoConfig)).thenReturn(ByteArrayOutputStream())
            val resume1 = Mockito.mock(Resume::class.java)
            Mockito.`when`(resume1.getResume(springMongoConfig)).thenReturn(ByteArrayOutputStream())
            val listCrunchers = ArrayList<Cruncher>()
            listCrunchers.add(cruncherImpl)
            Mockito.`when`(allCrunchers!!.getCrunchers()).thenReturn(listCrunchers)
            cruncher!!.postConstructor()
            cruncher!!.addWork(resume)
            cruncher!!.addWork(resume1)
            cruncher!!.stop()
            cruncher!!.join()
            Mockito.verify<Any>(allCrunchers, Mockito.times(2)).getCrunchers()
            Mockito.verify<Any>(resumeRepository).save(resume)
            Mockito.verify<Any>(resumeRepository).save(resume1)
            Mockito.verify<Any>(resume).getResume(springMongoConfig)
            Mockito.verify<Any>(resume1).getResume(springMongoConfig)
        }

        @Test
        @Throws(ResumeNotFound::class, ResumeReadException::class, InterruptedException::class)
        fun unableToFindResumeFile() {
            val resume = Mockito.mock(Resume::class.java)
            Mockito.`when`(resume.getResume(springMongoConfig)).thenThrow(ResumeNotFound(""))
            val listCrunchers = ArrayList<Cruncher>()
            listCrunchers.add(cruncherImpl)
            Mockito.`when`(allCrunchers!!.getCrunchers()).thenReturn(listCrunchers)
            cruncher!!.postConstructor()
            cruncher!!.addWork(resume)
            cruncher!!.stop()
            cruncher!!.join()
            Mockito.verify<Any>(allCrunchers, Mockito.times(0)).getCrunchers()
            Mockito.verify<Any>(resumeRepository, Mockito.times(0)).save(resume)
            Mockito.verify<Any>(resume).getResume(springMongoConfig)
        }

        @Test
        @Throws(ResumeNotFound::class, ResumeReadException::class, InterruptedException::class)
        fun unableToReadResumeFile() {
            val resume = Mockito.mock(Resume::class.java)
            Mockito.`when`(resume.getResume(springMongoConfig)).thenThrow(ResumeReadException(""))
            val listCrunchers = ArrayList<Cruncher>()
            listCrunchers.add(cruncherImpl)
            Mockito.`when`(allCrunchers!!.getCrunchers()).thenReturn(listCrunchers)
            cruncher!!.postConstructor()
            cruncher!!.addWork(resume)
            cruncher!!.stop()
            cruncher!!.join()
            Mockito.verify<Any>(allCrunchers, Mockito.times(0)).getCrunchers()
            Mockito.verify<Any>(resumeRepository, Mockito.times(0)).save(resume)
            Mockito.verify<Any>(resume).getResume(springMongoConfig)
        }
    }
}