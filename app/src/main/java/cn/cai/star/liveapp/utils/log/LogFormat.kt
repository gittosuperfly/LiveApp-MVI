package cn.cai.star.liveapp.utils.log

import cn.cai.star.liveapp.utils.log.model.LineRules
import java.util.regex.Pattern

class LogFormat(format: String, regex: String) {

    val lineRules: ArrayList<LineRules> = ArrayList()

    init {
        var msgIndex = -1
        val lines = format.lines()
        for (index in lines.indices) {
            if (!lines[index].contains("#M")) {
                continue
            }

            if (msgIndex != -1) {
                throw RuntimeException("Find [#M] in 'formatStyle' more than on times. but it requires only once!")
            }

            msgIndex = index
        }
        if (msgIndex == -1) {
            throw RuntimeException("Could not find formatStyle-style : [#T] in formatStyle-String")
        }

        for (index in lines.indices) {
            val origin = lines[index]

            val ruleSet = mutableSetOf<Pair<Int, String>>()
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(origin)
            while (matcher.find()) {
                val name = matcher.group()
                val indexOf = origin.indexOf(name)
                ruleSet.add(Pair(indexOf, name))
            }
            lineRules.add(LineRules(origin, ruleSet, index == msgIndex))
        }
    }
}