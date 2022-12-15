package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun charsConv(): Map<Char, Int> {
    val charsConv = mapOf<Char, Int>(
        '0' to 0,
        '1' to 1,
        '2' to 2,
        '3' to 3,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 7,
        '8' to 8,
        '9' to 9,
        'a' to 10,
        'b' to 11,
        'c' to 12,
        'd' to 13,
        'e' to 14,
        'f' to 15,
        'g' to 16,
        'h' to 17,
        'i' to 18,
        'j' to 19,
        'k' to 20,
        'l' to 21,
        'm' to 22,
        'n' to 23,
        'o' to 24,
        'p' to 25,
        'q' to 26,
        'r' to 27,
        's' to 28,
        't' to 29,
        'u' to 30,
        'v' to 31,
        'w' to 32,
        'x' to 33,
        'y' to 34,
        'z' to 35
    )
    return charsConv
}

fun keyFromValue(targetValue: Int): String? {
    for ((key, value ) in charsConv()) {
        if (value == targetValue) {
            return key.toString()
        }
    }
    return null
}

fun toDecimal(sourceBase: Int, inputNumberString: String): BigInteger {
    // check the input number
    val keysCharsConv: List<Char> = charsConv().keys.toList()
    val keysCharsConvSublist = keysCharsConv.subList(0, sourceBase)
    var wrongCharCount = 0
    var resultTo = BigInteger.valueOf(0)
    for (i in inputNumberString) {
        if (!keysCharsConvSublist.joinToString("").contains(i)) {
            wrongCharCount++
        }
    }
    if (wrongCharCount > 0) {
        println("The input number does not belong to base $sourceBase")
    } else {
        // transform to decimal
        for (i in inputNumberString.reversed().indices) {
            resultTo += charsConv()[inputNumberString.reversed()[i]]!!.toBigInteger() * (sourceBase.toBigInteger().pow(i))
        }
    }
    return resultTo
}

fun fromDecimal(targetBase: Int, number: BigInteger): String {
    // conversion to target base only
    var remainder = number
    var resultFrom = ""
    while (remainder > 0.toBigInteger()) {
        val digit = remainder % targetBase.toBigInteger()
        resultFrom += keyFromValue(digit.toInt())
        remainder = (remainder - digit) / targetBase.toBigInteger()
    }
    return resultFrom.reversed()
}

fun toDecimalFraction(sourceBase: Int, inputNumberString: String): BigDecimal {
    // check the input number
    val keysCharsConv: List<Char> = charsConv().keys.toList()
    val keysCharsConvSublist = keysCharsConv.subList(0, sourceBase)
    val (inputNumberStringIntegerPart, inputNumberStringFractionPart) = inputNumberString.split(".")
    val noPointString = inputNumberStringIntegerPart + inputNumberStringFractionPart
    var integerPart = BigDecimal.valueOf(0)
    var fractionPart = BigDecimal.valueOf(0)
    var wrongCharCount = 0
    var resultTo = BigDecimal.valueOf(0)
    for (i in noPointString) {
        if (!keysCharsConvSublist.joinToString("").contains(i)) {
            wrongCharCount++
        }
    }
    if (wrongCharCount > 0) {
        println("The input number does not belong to base $sourceBase")
    } else {
        // split integer and decimal parts
        if (inputNumberStringIntegerPart == "" || inputNumberStringIntegerPart == "0") {
            integerPart = BigDecimal.valueOf(0)
        } else {
            // transform integer part to decimal
            for (i in inputNumberStringIntegerPart.reversed().indices) {
                integerPart += charsConv()[inputNumberStringIntegerPart.reversed()[i]]!!.toBigDecimal() * (sourceBase.toBigDecimal().pow(i))
            }
        }
        if (inputNumberStringFractionPart == "" || inputNumberStringFractionPart == "0") {
            fractionPart = BigDecimal.valueOf(0.0)
        } else {
            // transform fraction part to decimal
            for (i in inputNumberStringFractionPart.indices) {
                fractionPart += charsConv()[inputNumberStringFractionPart[i]]!!.toBigDecimal().setScale(10, RoundingMode.FLOOR) / (sourceBase.toBigDecimal().pow(i + 1))
            }
        }
        resultTo = integerPart + fractionPart
    }
    return resultTo
}

fun fromDecimalFraction(targetBase: Int, number: BigDecimal): String {
    val (integerPartString, fractionPartString) = number.toString().split(".")
    var resultFrom = ""
    // converting integer part
    var integerResult = ""
    var remainder = integerPartString.toBigDecimal()
    while (remainder > 0.toBigDecimal()) {
        val digit = remainder % targetBase.toBigDecimal()
        integerResult += keyFromValue(digit.toInt())
        remainder = (remainder - digit) / targetBase.toBigDecimal()
    }
    // converting fraction part
    val fractionNumber = BigDecimal("0.$fractionPartString")
    var countAfterZero = 5
    var fractionAux = fractionNumber
    var fractionResult = ""
    while (countAfterZero > 0 || fractionAux == 0.toBigDecimal()) {
        val multResult = fractionAux * targetBase.toBigDecimal()
        if (multResult.toString().contains(".")) {
            val (integerMultString, fractionMultString) = multResult.toString().split(".")
            fractionResult += keyFromValue(integerMultString.toInt())
            fractionAux = BigDecimal("0.$fractionMultString")
            countAfterZero--
        } else {
            fractionResult += "0"
            countAfterZero--
        }
    }
    if (integerResult == "") {
        resultFrom = "0.$fractionResult"
    } else {
        resultFrom = "${integerResult.reversed()}.${fractionResult}"
    }
    return resultFrom
}

fun main() {
    do {
        // first stage question in cycle
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val userInput1 = readln().lowercase()
        if (userInput1 != "/exit") {
            try {
                // transform and check the input into two numbers
                val (sourceBase, targetBase) = userInput1.split(" ").map { it.toInt() }
                if (sourceBase in 2..36 && targetBase in 2..36) {
                    do {
                        // second stage question in cycle
                        print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
                        val userInput2 = readln().lowercase()
                        if (userInput2 != "/back" && !userInput2.contains(".")) {
                            try {
                                // check and transform to decimal
                                val finalResult = fromDecimal(targetBase, toDecimal(sourceBase, userInput2))
                                println("Conversion result: $finalResult")
                            } catch (e: Exception) {
                                println("Enter the correct value")
                            }
                        } else if (userInput2.contains(".")) {
                            try {
                                // check fraction and transform to decimal fraction
                                val finalResult = fromDecimalFraction(targetBase, toDecimalFraction(sourceBase, userInput2))
                                println("Conversion result: $finalResult")
                            } catch (e: Exception) {
                                println("Enter the correct value")
                            }
                        }
                    } while (userInput2 != "/back")
                } else {
                    println("The source base and the target base must be within 2...36")
                }
            } catch (e: Exception) {
                println("Wrong input, please try again.")
            }
        }
    } while (userInput1 != "/exit")
}
