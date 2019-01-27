package org.metplus.cruncher.resume

import org.metplus.cruncher.rating.ProcessCruncher
import java.io.InputStream

class UploadResume(
        private val resumeRepository: ResumeRepository,
        private val resumeFileRepository: ResumeFileRepository,
        private val crunchResumeProcess: ProcessCruncher<Resume>
) {
    fun process(userId: String, resumeName: String, file: InputStream, size: Long, observer: UploadResumeObserver<*>): Any {
        val fileType = resumeName.split(".").last()
        val resume = Resume(
                userId = userId,
                filename = resumeName,
                fileType = fileType,
                cruncherData = mutableMapOf()
        )

        if (size == 0.toLong())
            return observer.onEmptyFile(resume)!!

        return try {
            resumeFileRepository.save(ResumeFile(
                    filename = resumeName,
                    userId = userId,
                    fileStream = file
            ))
            val savedResume = resumeRepository.save(resume)
            crunchResumeProcess.addWork(savedResume)
            observer.onSuccess(savedResume)!!
        } catch (exception: Exception) {
            resumeFileRepository.deleteIfExists(userId)
            observer.onException(exception, resume)!!
        }
    }
}

interface UploadResumeObserver<T> {
    fun onSuccess(resume: Resume): T
    fun onEmptyFile(resume: Resume): T
    fun onException(exception: Exception, resume: Resume): T
}
