package org.metplus.cruncher.rating

import org.metplus.cruncher.error.DocumentParseException
import org.metplus.cruncher.resume.Resume
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeNotFound
import org.metplus.cruncher.resume.ResumeRepository
import org.metplus.cruncher.utilities.DocumentParserImpl
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream

open class CrunchResumeProcess(
        private val allCrunchers: CruncherList,
        private val resumeRepository: ResumeRepository,
        private val resumeFileRepository: ResumeFileRepository
) : ProcessCruncher<Resume>() {

    override fun process(resume: Resume) {
        logger.info("Going to process the resume for: " + resume.userId)
        try {
            val resumeFile = resumeFileRepository.getByUserId(resume.userId)
            val outputFile = ByteArrayOutputStream()
            var data = resumeFile.fileStream.read()
            while (data >= 0) {
                outputFile.write(data.toChar().toInt())
                data = resumeFile.fileStream.read()
            }

            val docParser = DocumentParserImpl(outputFile)
            docParser.parse()
            logger.info("Document: " + docParser.getDocument()?.replace("\n", "\\\\n")?.replace("\t", " "))
            val allMetaData = mutableMapOf<String, CruncherMetaData>()
            allCrunchers.getCrunchers().forEach {
                val metaData = it.crunch(docParser.getDocument()!!)
                allMetaData[it.getCruncherName()] = metaData
            }

            resumeRepository.save(resume.copy(cruncherData = allMetaData))
            logger.info("Done processing resume for: " + resume.userId)
        } catch (resumeNotFound: ResumeNotFound) {
            resumeNotFound.printStackTrace()
            logger.error("Unable to find the resume: $resume")
        } catch (e: DocumentParseException) {
            e.printStackTrace()
            logger.error("Problem Parsing the resume: $resume. $e")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Problem processing the resume: $resume. $e")
        }

        logger.info("Ended process of: " + resume.userId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CrunchResumeProcess::class.java)
    }
}
