package hr.ferit.matijasokol.coinmarket.ui.fragments.details

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.models.CoinInfoResponse
import hr.ferit.matijasokol.coinmarket.models.Resource
import hr.ferit.matijasokol.coinmarket.other.*
import hr.ferit.matijasokol.coinmarket.other.Constants.CHART_ANIM_DURATION
import hr.ferit.matijasokol.coinmarket.other.Constants.WEEK_LENGTH
import kotlinx.android.synthetic.main.fragment_details.*
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var coin: Coin

    private val viewModel: DetailsViewModel by viewModels()

    private val dailyValues by lazy { mutableListOf<Entry>() }
    private val weekValues by lazy { mutableListOf<Entry>() }
    private val monthValues by lazy { mutableListOf<Entry>() }
    private val yearValues by lazy { mutableListOf<Entry>() }

    private val textViewsChoosers by lazy { listOf(textViewDay, textViewWeek, textViewMonth, textViewYear) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSharedElementTransitionOnEnter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coin = args.coin

        imageViewIconDetails.apply {
            transitionName = coin.imageUrl
            loadImage(coin.imageUrl)
        }

        setListeners()
        setTextViewsClickability(false)
        observeChanges()

        viewModel.getCoinDetails(coin.id)
    }

    private val TAG = "[DEBUG] DetailsFra"

    private fun setListeners() {
        textViewDay.setOnClickListener {
            setTextViewsColor(textViewDay)
            try {
                setLineChart(dailyValues)
            } catch (e: Exception) {
                Log.d(TAG, "daily")
                detailsRootLayout.showSnackbar("daily")
            }
        }
        textViewWeek.setOnClickListener {
            setTextViewsColor(textViewWeek)
            try {
                setLineChart(weekValues)
            } catch (e: Exception) {
                Log.d(TAG, "week")
                detailsRootLayout.showSnackbar("week")
            }
        }
        textViewMonth.setOnClickListener {
            setTextViewsColor(textViewMonth)
            try {
                setLineChart(monthValues)
            } catch (e: Exception) {
                Log.d(TAG, "month")
                detailsRootLayout.showSnackbar("month")
            }
        }
        textViewYear.setOnClickListener {
            setTextViewsColor(textViewYear)
            try {
                setLineChart(yearValues)
            } catch (e: Exception) {
                Log.d(TAG, "year")
                detailsRootLayout.showSnackbar("year")
            }
        }
    }

    private fun observeChanges() {
        viewModel.lastDayCoinDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        setDailyData(it)
                    }
                    lottieChart.gone()
                    lineChart.visible()
                    setTextViewsClickability(true)
                    setTextViewsColor(textViewDay)
                }
                is Resource.Error -> {
                    lottieChart.gone()
                    response.message?.let {
                        detailsRootLayout.showSnackbar(getString(R.string.error))
                    }
                }
                is Resource.Loading -> {
                    lottieChart.visible()
                }
            }
        })

        viewModel.coinInfo.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        setTextViews(it)
                    }
                    lottieText.gone()
                }
                is Resource.Error -> {
                    lottieText.gone()
                    response.message?.let {
                        detailsRootLayout.showSnackbar(getString(R.string.error))
                    }
                }
                is Resource.Loading -> {
                    lottieText.visible()
                }
            }
        })

        viewModel.yearCoinDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        setWeekData(it)
                        setYearData(it)
                        setMonthData(it)
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        detailsRootLayout.showSnackbar(getString(R.string.error))
                    }
                }
                is Resource.Loading -> {
                    lottieChart.visible()
                }
            }
        })
    }

    private fun setTextViews(response: CoinInfoResponse) {
        val highPriceText = "${getString(R.string.high_24)}: ${coin.maxPrice}€"
        val lowPriceText = "${getString(R.string.low_24)}: ${coin.minPrice}€"
        val hashAlgText = "${getString(R.string.hash_alg)}: ${response.hashAlgorithm ?: getString(R.string.unknown)}"

        val highPriceSpannable = SpannableString(highPriceText).apply {
            setSpan(ForegroundColorSpan(Color.GREEN), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val lowPriceSpannable = SpannableString(lowPriceText).apply {
            setSpan(ForegroundColorSpan(Color.RED), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val hashAlgSpannable = SpannableString(hashAlgText).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textViewDesc.text = response.description.english.trim().fromHtmlToText()
        textViewHighPrice.apply {
            text = highPriceSpannable
            visible()
        }
        textViewLowPrice.apply {
            text = lowPriceSpannable
            visible()
        }
        textViewHashAlg.apply {
            text = hashAlgSpannable
            visible()
        }
    }

    private fun setTextViewsColor(choosenTextView: TextView) {
        textViewsChoosers.first { it == choosenTextView }.background = ContextCompat.getDrawable(requireContext(), R.drawable.text_view_shape_full)
        textViewsChoosers.filterNot { it == choosenTextView }.forEach { it.background = ContextCompat.getDrawable(requireContext(), R.drawable.text_view_shape_empty) }
    }

    private fun setTextViewsClickability(clickable: Boolean) {
        textViewsChoosers.forEach { it.isClickable = clickable }
    }

    private fun setDailyData(data: List<Float>) {
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        for (i in 23 downTo 0) {
            var hour = currentHour - i
            if (hour < 1) hour += 24
            if (hour == 24) hour = 0
            dailyValues.add(Entry(hour.toFloat(), data[23 - i]))
        }

        setLineChart(dailyValues)
    }

    private fun setWeekData(data: List<Float>) {
        getDailyData(WEEK_LENGTH, weekValues, data)
    }

    private fun setMonthData(data: List<Float>) {
        getDailyData(getPreviousMonthLength(), monthValues, data)
    }

    private fun getDailyData(range: Int, list: MutableList<Entry>, data: List<Float>) {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val lastMonthLen = getPreviousMonthLength()

        for (i in (range - 1) downTo 0) {
            var day = currentDay - i
            if (day < 1) {
                day = lastMonthLen - i + currentDay
                val index = data.size - 1 - i
                if (index < 0) {
                    return
                }
                list.add(Entry(day.toFloat(), data[index]))
            } else {
                list.add(Entry(day.toFloat(), data[data.size - 1 - i]))
            }
        }
    }

    private fun setYearData(data: List<Float>) {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(Calendar.MONTH)
        var firstMonth = true
        var lastDay = 0
        var monthOverflow = 0

        for (i in 0..11) {
            if (firstMonth) {
                val currentMonthValues = data.reversed().subList(0, currentDay)
                lastDay += currentMonthValues.size
                val avg = currentMonthValues.average().toFloat()
                yearValues.add(Entry((currentMonth + 1).toFloat(), avg))
                firstMonth = false
            } else {
                var month = currentMonth - yearValues.size + 1
                if (month < 1) {
                    monthOverflow++
                    month = 12 - monthOverflow
                    val monthLen = GregorianCalendar(cal.get(Calendar.YEAR - 1), month, 1).getActualMaximum(Calendar.DAY_OF_MONTH)
                    val currentMonthValues = if (lastDay + monthLen > data.size) {
                        data.reversed().subList(lastDay, data.size)
                    } else {
                        data.reversed().subList(lastDay, lastDay + monthLen)
                    }
                    lastDay += currentMonthValues.size
                    val avg = currentMonthValues.average().toFloat()
                    yearValues.add(Entry((month + 1).toFloat(), avg))
                } else {
                    val monthLen = GregorianCalendar(cal.get(Calendar.YEAR), currentMonth - i, 1).getActualMaximum(Calendar.DAY_OF_MONTH)
                    val currentMonthValues = if (lastDay + monthLen > data.size) {
                        data.reversed().subList(lastDay, data.size)
                    } else {
                        data.reversed().subList(lastDay, lastDay + monthLen)
                    }
                    lastDay += currentMonthValues.size
                    val avg = currentMonthValues.average().toFloat()
                    yearValues.add(Entry((currentMonth + 1 - i).toFloat(), avg))
                }
            }
        }

        yearValues.reverse()
    }

    private fun setSharedElementTransitionOnEnter() {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    private fun setLineChart(data: List<Entry>) {
        if (data.isEmpty() || data.map { it.y }.any { it.isNaN() }) {
            detailsRootLayout.showSnackbar(getString(R.string.no_values_category))
        }

        val changedData = mutableListOf<Entry>()
        data.forEach {
            changedData.add(Entry(data.indexOf(it).toFloat(), it.y))
        }

        lineChart.apply {
            description.isEnabled = false
            setBackgroundColor(Color.WHITE)
            setTouchEnabled(true)
            setDrawGridBackground(true)

            val xAxisValues = data.map { it.x.toInt().toString() }
            val markerView = CustomMarkerView(requireContext(), R.layout.custom_marker_view, xAxisValues, changedData)
            markerView.chartView = this
            marker = markerView

            isDragEnabled = true
            setScaleEnabled(true)

            setPinchZoom(true)

            xAxis.apply {
                enableGridDashedLine(10f, 10f, 0f)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(xAxisValues)
            }

            axisLeft.apply {
                lineChart.axisRight.isEnabled = false
                enableGridDashedLine(10f, 10f, 0f)
                axisMaximum = changedData.map { it.y }.max() ?: 0f
                axisMinimum = 0f
            }

            val lineSet: LineDataSet

            if (getData() != null && getData().dataSetCount > 0) {
                lineSet = getData().getDataSetByIndex(0) as LineDataSet
                lineSet.values = changedData
                lineSet.fillAlpha = 110
                lineSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_background)
                lineSet.mode = LineDataSet.Mode.CUBIC_BEZIER;
                lineSet.notifyDataSetChanged()
                getData().notifyDataChanged()
                notifyDataSetChanged()
            } else {
                lineSet = LineDataSet(changedData, coin.name)
                lineSet.setDrawIcons(false)
                lineSet.setDrawValues(false)
                lineSet.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                lineSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                lineSet.setDrawFilled(true)
                lineSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_background)
                lineSet.enableDashedLine(10f, 5f, 0f)
            }

            val list: ArrayList<ILineDataSet> = ArrayList()
            list.add(lineSet)
            val lineData = LineData(list)
            setData(lineData)

            animateX(CHART_ANIM_DURATION, Easing.Linear)
        }
    }

}