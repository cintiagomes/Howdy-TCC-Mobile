import java.text.SimpleDateFormat
import java.util.*

fun convertBrStringToDate(brazilDate: String) : Date {
    val formatter = SimpleDateFormat("dd/MM/yyyy")

    return formatter.parse(brazilDate)
}

fun convertDateToBrString(date: Date): String{
    val formatter = SimpleDateFormat("dd/MM/yyyy")

    return formatter.format(date)
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
    val oneMonth = 2629743833.3
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
        return "Há $distanceInWeeks semana" + if(distanceInWeeks > 1) "s." else "."
    }

    if (distanceTimeInMilliseconds < oneYear){
        val distanceInMonths = (distanceTimeInMilliseconds / oneMonth).toInt()
        return "Há $distanceInMonths " + if(distanceInMonths > 1) "meses." else "mês."
    }

    val distanceInYears = (distanceTimeInMilliseconds / oneYear).toInt()
    return "Há $distanceInYears ano" + if(distanceInYears > 1) "s." else "."
}

//fun convertBackEndDateTimeFormatToBrazilianLongDate(backendDateTime: Date) : String {
//    val f = SimpleDateFormat("dd de MMM de yyyy")
//        val d = f.parse(string_date)
//        val milliseconds = d.time
//    } catch (e: ParseException) {
//        e.printStackTrace()
//    }
//   return backendDateTime.
//}