package hr.ferit.matijasokol.coinmarket.other

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import hr.ferit.matijasokol.coinmarket.R
import org.jsoup.Jsoup
import java.text.DecimalFormat
import java.util.*

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.placeholder)
        .into(this)
}

fun Float.roundTo(decimalPlaces: Int) = "%.${decimalPlaces}f".format(Locale.ENGLISH,this).toFloat()

fun Float.formatNumber(): String = DecimalFormat("#,###,###.##").format(this)

fun String.fromHtmlToText(): String = Jsoup.parse(this).text()

fun View.showSnackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) = Snackbar.make(this, message, length).show()