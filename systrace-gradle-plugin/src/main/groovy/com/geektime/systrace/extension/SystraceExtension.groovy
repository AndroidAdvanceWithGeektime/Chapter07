package com.geektime.systrace.extension

class SystraceExtension {
    boolean enable
    String baseMethodMapFile
    String blackListFile
    String output


    SystraceExtension() {
        enable = true
        baseMethodMapFile = ""
        blackListFile = ""
        output = ""

    }

    @Override
    String toString() {
        """| enable = ${enable}
           | baseMethodMapFile = ${baseMethodMapFile}
           | blackListFile = ${blackListFile}
           | output = ${output}
        """.stripMargin()
    }
}