package org.metplus.cruncher.rating

class CruncherStub: Cruncher {
    private var cruncherName = "some-cruncher"
    var crunchReturn: MutableList<CruncherMetaData> = mutableListOf()
    var crunchWasCalledWith: MutableList<String> = mutableListOf()
    override fun getCruncherName(): String {
        return cruncherName
    }

    override fun crunch(data: String): CruncherMetaData {
        if(crunchReturn.size == 0)
            throw Exception("Stub called when it was not expected")
        crunchWasCalledWith.add(data)
        return crunchReturn[crunchWasCalledWith.size - 1]
    }
}