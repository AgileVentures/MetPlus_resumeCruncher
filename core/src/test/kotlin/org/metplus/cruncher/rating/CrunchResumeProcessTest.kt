package org.metplus.cruncher.rating


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.resume.FileInputStreamFake
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeFile
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeFileRepositoryFake
import org.metplus.cruncher.resume.ResumeNotFound
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.resume.ResumeRepositoryFake
import java.io.ByteArrayOutputStream

class CrunchResumeProcessTest {
    private lateinit var cruncher: CrunchResumeProcess
    private lateinit var cruncherImpl: CruncherStub
    private lateinit var resumeRepository: ResumeRepository
    private lateinit var resumeFileRepository: ResumeFileRepository

    @BeforeEach
    fun setup() {
        cruncherImpl = CruncherStub()
        val allCrunchers = CruncherList()
        allCrunchers.addCruncher(cruncherImpl)
        resumeRepository = ResumeRepositoryFake()
        resumeFileRepository = ResumeFileRepositoryFake()
        cruncher = CrunchResumeProcess(
                allCrunchers = allCrunchers,
                resumeRepository = resumeRepository,
                resumeFileRepository = resumeFileRepository
        )
    }

    @Test
    fun `When no resumes need to be processed, starts and stops without calling the cruncher`() {
        cruncher.start()
        cruncher.stop()
        cruncher.join()
    }

    @Test
    fun `When one resume need to be processed, it gets crunched`() {
        val resume = resumeRepository.save(
                Resume(
                        filename = "some-file-name.pdf",
                        userId = "some-user-id",
                        fileType = "pdf",
                        cruncherData = CruncherMetaData(mutableMapOf<String, Double>() as HashMap<String, Double>))
        )
        resumeFileRepository.save(ResumeFile(
                filename = "some-file-name.pdf",
                userId = "some-user-id",
                fileStream = FileInputStreamFake("Some text in the file")
        ))
        cruncherImpl.crunchReturn = CruncherMetaData(
                metaData = mutableMapOf("some-key" to 10.0) as HashMap<String, Double>
        )
        cruncher.start()
        cruncher.addWork(resume)
        cruncher.stop()
        cruncher.join()
        assertThat(cruncherImpl.crunchWasCalledWith).contains("Some text in the file")
        val cruncherResume = resumeRepository.getByUserId("some-user-id")!!
        assertThat(cruncherResume.cruncherData.metaData["some-key"]).isEqualTo(10.0)
    }
//
//    @Test
//    @Throws(ResumeNotFound::class, ResumeReadException::class, InterruptedException::class)
//    fun twoResume() {
//        val resume = Mockito.mock(Resume::class.java)
//        Mockito.`when`(resume.getResume(springMongoConfig)).thenReturn(ByteArrayOutputStream())
//        val resume1 = Mockito.mock(Resume::class.java)
//        Mockito.`when`(resume1.getResume(springMongoConfig)).thenReturn(ByteArrayOutputStream())
//        val listCrunchers = ArrayList<Cruncher>()
//        listCrunchers.add(cruncherImpl)
//        Mockito.`when`(allCrunchers!!.getCrunchers()).thenReturn(listCrunchers)
//        cruncher!!.postConstructor()
//        cruncher!!.addWork(resume)
//        cruncher!!.addWork(resume1)
//        cruncher!!.stop()
//        cruncher!!.join()
//        Mockito.verify(allCrunchers, Mockito.times(2)).getCrunchers()
//        Mockito.verify(resumeRepository).save(resume)
//        Mockito.verify(resumeRepository).save(resume1)
//        Mockito.verify(resume).getResume(springMongoConfig)
//        Mockito.verify(resume1).getResume(springMongoConfig)
//    }
//
//    @Test
//    @Throws(ResumeNotFound::class, ResumeReadException::class, InterruptedException::class)
//    fun unableToFindResumeFile() {
//        val resume = Mockito.mock(Resume::class.java)
//        Mockito.`when`(resume.getResume(springMongoConfig)).thenThrow(ResumeNotFound(""))
//        val listCrunchers = ArrayList<Cruncher>()
//        listCrunchers.add(cruncherImpl)
//        Mockito.`when`(allCrunchers!!.getCrunchers()).thenReturn(listCrunchers)
//        cruncher!!.postConstructor()
//        cruncher!!.addWork(resume)
//        cruncher!!.stop()
//        cruncher!!.join()
//        Mockito.verify(allCrunchers, Mockito.times(0)).getCrunchers()
//        Mockito.verify(resumeRepository, Mockito.times(0)).save(resume)
//        Mockito.verify(resume).getResume(springMongoConfig)
//    }
//
//    @Test
//    @Throws(ResumeNotFound::class, ResumeReadException::class, InterruptedException::class)
//    fun unableToReadResumeFile() {
//        val resume = Mockito.mock(Resume::class.java)
//        Mockito.`when`(resume.getResume(springMongoConfig)).thenThrow(ResumeReadException(""))
//        val listCrunchers = ArrayList<Cruncher>()
//        listCrunchers.add(cruncherImpl)
//        Mockito.`when`(allCrunchers!!.getCrunchers()).thenReturn(listCrunchers)
//        cruncher!!.postConstructor()
//        cruncher!!.addWork(resume)
//        cruncher!!.stop()
//        cruncher!!.join()
//        Mockito.verify(allCrunchers, Mockito.times(0)).getCrunchers()
//        Mockito.verify(resumeRepository, Mockito.times(0)).save(resume)
//        Mockito.verify(resume).getResume(springMongoConfig)
//    }
}
