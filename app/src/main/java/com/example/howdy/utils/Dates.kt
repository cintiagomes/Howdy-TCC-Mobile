import java.time.LocalDate
import java.io.*;
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