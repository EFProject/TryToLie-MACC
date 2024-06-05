package com.example.trytolie.game.model.game.speechParser

import java.util.Locale


object SpeechParser{

    fun parseSpeechToDeclaration(textSpoken: String): String? {

        val textSpokenParsed = parseString(textSpoken)
        if (textSpokenParsed.isNullOrEmpty()){
            return null
        }

        return textSpokenParsed
    }

    private fun parseString(textSpoken: String): String? {
        val occurrence: String
        val value: String
        if (textSpoken.length >= 2 && textSpoken.contains(' ')){
            occurrence = convertNumericWordsToDigits(textSpoken.split(" ")[0])
            value = convertNumericWordsToDigits(textSpoken.split(" ")[1])
        } else if (textSpoken.length == 2) {
            occurrence = convertNumericWordsToDigits(textSpoken[0].toString())
            value = convertNumericWordsToDigits(textSpoken[1].toString())
        } else {
            return null
        }

        return if((occurrence != "") and (value != "")){
            "${occurrence}${value}".lowercase()
        } else {
            null
        }

    }

    private fun convertNumericWordsToDigits(textSpoken: String): String {

        val wordToDigitMap = mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "uno" to "1",
            "due" to "2",
            "tre" to "3",
            "quattro" to "4",
            "cinque" to "5",
            "sei" to "6",
            "1" to "1",
            "2" to "2",
            "3" to "3",
            "4" to "4",
            "5" to "5",
            "6" to "6",
        )

        return wordToDigitMap[textSpoken.lowercase(Locale.getDefault())] ?: ""

    }

    fun checkDeclaration(
        declaration: String?,
        diceNumber: Int,
        declarationResults: List<Int> ): String {

        if (declaration.isNullOrEmpty()){
            return "Retry"
        }

        val occurrence = declaration[0].toString().toInt()
        val value = declaration[1].toString().toInt()
        if(occurrence > diceNumber) return "You have only $diceNumber dice"

        if(declarationResults.isNotEmpty()){
            val preOccurrence = declarationResults[0]
            val preValue = declarationResults[1]
            if(occurrence > preOccurrence &&  value == preValue) return "OK"
            if(occurrence <= preOccurrence && value > preValue) return "OK"

        } else return "OK"

        return "You must make a declaration higher than the previous"
    }

}