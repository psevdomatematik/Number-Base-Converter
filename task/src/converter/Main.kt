package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun fromDecimalInt(num: BigInteger, targetBase: BigInteger): String {
    var result = ""
    var intPart = num
    do {
        result += if (intPart % targetBase > BigInteger("9")) ((intPart % targetBase).toInt() + 87).toChar() else intPart % targetBase
        intPart /= targetBase
    } while (intPart != BigInteger.ZERO)
    return result.replace(" ", "")
}

fun fromDecimalFrac(num: String, targetBase: BigDecimal): String {
    var result = ""
    var fracPart = BigDecimal("0.$num")
    var intPart: BigDecimal
    repeat(5) {
        fracPart *= targetBase
        intPart = fracPart.setScale(0,RoundingMode.DOWN)
        result += if (intPart > BigDecimal("9")) (intPart.toInt() + 87).toChar() else intPart
        fracPart -= intPart
    }
    return result
}

fun toDecimalInt(num: String, sourceBase: BigInteger): BigInteger {
    var sum = BigInteger.ZERO
    var degree = 0
    for (i in num.lastIndex downTo 0) {
        var prod = BigInteger.ONE
        repeat(degree) {
            prod *= sourceBase
        }
        sum += if (num[i] >= 'a') (num[i].code - 87).toBigInteger()  * prod else BigInteger(num[i].toString()) * prod
        degree++
    }
    return sum
}

fun toDecimalFrac(num: String, sourceBase: BigDecimal): BigDecimal {
    var sum = BigDecimal.ZERO
    for (i in 0..num.lastIndex) {
        val prod = BigDecimal.ONE.setScale(10) / sourceBase.pow(i + 1)
        sum += if (num[i] >= 'a') (num[i].code - 87).toBigDecimal()  * prod else BigDecimal(num[i].toString()) * prod
    }
    return sum
}

fun main() {
    print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
    var userChoice = readLine()!!
    while (userChoice != "/exit") {
        val sourceBase = userChoice.split(" ")[0]
        val targetBase = userChoice.split(" ")[1]
        do {
            print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
            userChoice = readLine()!!
            if (userChoice == "/back") break
            val intPart = userChoice.split(".")[0]
            val fractPart = if ('.' in userChoice) userChoice.split(".")[1] else ""
            if (sourceBase == "10") {
                val resultInt = fromDecimalInt(BigInteger(intPart), BigInteger(targetBase))
                print("Conversion result: ")
                for (i in resultInt.lastIndex downTo 0) print(resultInt[i])
                if (fractPart != "") {
                    val resultFract = fromDecimalFrac(fractPart, BigDecimal(targetBase))
                    print(".$resultFract")
                }
            }
            else if (targetBase == "10") {
                var sum = BigDecimal.ZERO
                val sumInt = toDecimalInt(intPart, BigInteger(sourceBase))
                sum += sumInt.toBigDecimal()
                if (fractPart != "") {
                    val resultFract = toDecimalFrac(fractPart, BigDecimal(sourceBase)).setScale(5, RoundingMode.DOWN)
                    sum += resultFract
                }
                println("Conversion result: $sum")
                println()
            }
            else {
                val sumInt = toDecimalInt(intPart, BigInteger(sourceBase))
                val resultInt = fromDecimalInt(sumInt, targetBase.toBigInteger())
                print("Conversion result: ")
                for (i in resultInt.lastIndex downTo 0) print(resultInt[i])
                if (fractPart != "") {
                    val resultFract = toDecimalFrac(fractPart, BigDecimal(sourceBase)).setScale(7, RoundingMode.DOWN)
                    val strResultFract = if ('.' in resultFract.toString()) fromDecimalFrac(resultFract.toString().split(".")[1], BigDecimal(targetBase)) else "00000"

                    print(".$strResultFract")
                }
            }
            println()
        } while (true)
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        userChoice = readLine()!!
    }
}