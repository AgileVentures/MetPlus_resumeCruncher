package org.metplus.cruncher.rating

class CruncherStub: Cruncher {
    private var cruncherName = "some-cruncher"
    var crunchReturn: MutableList<CruncherMetaData> = mutableListOf()
    var crunchWasCalledWith: MutableList<String> = mutableListOf()
    var trainWasCalledWith: Map<String, List<String>> = mutableMapOf()
    override fun getCruncherName(): String {
        return cruncherName
    }

    override fun crunch(data: String): CruncherMetaData {
        if(crunchReturn.size == 0)
            throw Exception("Stub called when it was not expected")
        crunchWasCalledWith.add(data)
        return crunchReturn[crunchWasCalledWith.size - 1]
    }

    override fun train(database: Map<String, List<String>>) {
        trainWasCalledWith = database
    }
}