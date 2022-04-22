import com.example.howdy.model.ErrorBackend
import java.util.*;
import java.lang.*;
import java.io.*;

fun hadAnError(response: String) : ErrorBackend {
    var error = ErrorBackend()

    if(response.contains("error")){
        val statusString = response.substring(6,9)
        error.status = Integer.parseInt(statusString)
        error.message = response.substring(21, response.length - 2)
    }

    return error
}