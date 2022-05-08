import java.text.SimpleDateFormat
import java.util.*

fun convertBrStringToDate(brazilDate: String) : Date {
    val formato = SimpleDateFormat("dd/MM/yyyy")

    return formato.parse(brazilDate)
}

fun convertDateToBackendFormat(date: Date) : String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")

    return formatter.format(date)
}

fun convertBackEndDateTimeFormatToSocialMediaFormat(backendDateTime: Date) : String {
    val now = Date()

    val distanceTimeInMilliseconds = now.time - backendDateTime.time
    val oneMinute = 60000
    val oneHour = 3600000
    val oneDay = 86400000
    val oneWeek = 604800016.56
    val oneMonth = 1702967296
    val oneYear = 31557600000

    if (distanceTimeInMilliseconds < oneMinute) {
        return "Há alguns segundos"
    }

    if (distanceTimeInMilliseconds < oneHour){
        val distanceInMinutes = (distanceTimeInMilliseconds / oneMinute).toInt()
        return "Há $distanceInMinutes minuto" + if(distanceInMinutes > 1) "s." else "."
    }

    if (distanceTimeInMilliseconds < oneDay){
        val distanceInHours = (distanceTimeInMilliseconds / oneHour).toInt()
        return "Há $distanceInHours hora" + if(distanceInHours > 1) "s." else "."
    }

    if (distanceTimeInMilliseconds < oneWeek){
        val distanceInDays = (distanceTimeInMilliseconds / oneDay).toInt()
        return "Há $distanceInDays dia"  + if(distanceInDays > 1) "s." else "."
    }

    if (distanceTimeInMilliseconds < oneMonth){
        val distanceInWeeks = (distanceTimeInMilliseconds / oneWeek).toInt()
        println("DEBUGANDO DEU SEMANAL" + distanceTimeInMilliseconds)
        return "Há $distanceInWeeks semana" + if(distanceInWeeks > 1) "s." else "."
    }

    if (distanceTimeInMilliseconds < oneYear){
        val distanceInMonths = (distanceTimeInMilliseconds / oneMonth).toInt()
        println("DEBUGANDO $distanceInMonths $distanceTimeInMilliseconds $oneMonth")
        return "Há $distanceInMonths " + if(distanceInMonths > 1) "meses." else "mês."
    }

    val distanceInYears = (distanceTimeInMilliseconds / oneYear).toInt()
    return "Há $distanceInYears ano" + if(distanceInYears > 1) "s." else "."
}