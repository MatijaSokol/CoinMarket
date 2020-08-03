package hr.ferit.matijasokol.coinmarket.ui.fragments.details

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import hr.ferit.matijasokol.coinmarket.other.roundTo
import kotlinx.android.synthetic.main.custom_marker_view.view.*


class CustomMarkerView(
    context: Context,
    layoutId: Int,
    private val xLabelValues: List<String>,
    private val entryList: List<Entry>
) : MarkerView(context, layoutId) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) {
            return
        }

        val index = entryList.indexOf(e)

        val text = "(${xLabelValues[index]}, ${e.y.roundTo(2)})"
        textViewContent.text = text

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }


}