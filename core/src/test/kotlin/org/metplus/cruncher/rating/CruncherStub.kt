package org.metplus.cruncher.rating

class CruncherStub: Cruncher {
    private var cruncherName = "some-cruncher"
    var crunchReturn: CruncherMetaData? = null
    var crunchWasCalledWith: String? = null
    override fun getCruncherName(): String {
        return cruncherName
    }

    override fun crunch(data: String): CruncherMetaData {
        crunchWasCalledWith = data
        return crunchReturn!!
    }
}