package org.metplus.cruncher.resume

import java.io.InputStream

class UploadResume(
        private val resumeRepository: ResumeRepository,
        private val resumeFileRepository: ResumeFileRepository
) {
    fun process(userId: String, resumeName: String, file: InputStream, size: Long, observer: UploadResumeObserver<*>): Any {
        val fileType = resumeName.split(".").last()
        val resume = Resume(
                userId = userId,
                filename = resumeName,
                fileType = fileType
        )

        if (size == 0.toLong())
            return observer.onEmptyFile(resume)!!

        return try {
            resumeFileRepository.save(ResumeFile(
                    filename = resumeName,
                    userId = userId,
                    fileStream = file
            ))
            observer.onSuccess(resumeRepository.save(resume))!!
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
