package hr.ferit.matijasokol.coinmarket.other

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.util.*

fun getPreviousMonthLength(): Int {
    val cal = Calendar.getInstance()
    val currentMonth = cal.get(Calendar.MONTH)
    val gregorianCalendar = GregorianCalendar(cal.get(Calendar.YEAR), currentMonth - 1, 1)
    return gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo?.run {
            return when(type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }
        }
    }
    return false
}