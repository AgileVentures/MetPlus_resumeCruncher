package org.metplus.cruncher.resume

class DownloadResume(
        private val resumeRepository: ResumeRepository,
        private val resumeFileRepository: ResumeFileRepository) {
    fun <T> process(userId: String, observer: DownloadResumeObserver<T>): T? {
        val resume = resumeRepository.getByUserId(userId) ?: return observer.onResumeNotFound(userId)

        return try {
            val resumeFile = resumeFileRepository.getByUserId(userId)
            observer.onSuccess(resume, resumeFile)
        } catch (exception: Exception) {
            observer.onError(userId, exception)
        }
    }
}

interface DownloadResumeObserver<T> {
    fun onSuccess(resume: Resume, resumeFile: ResumeFile): T
    fun onResumeNotFound(userId: String): T?
    fun onError(userId: String, exception: Exception): T
}
