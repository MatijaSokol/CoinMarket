package hr.ferit.matijasokol.coinmarket.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.other.formatNumber
import hr.ferit.matijasokol.coinmarket.other.loadImage
import hr.ferit.matijasokol.coinmarket.other.roundTo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_view.view.*

class CoinsAdapter(private val onItemClicked: (Coin, View) -> Unit) : ListAdapter<Coin, CoinsAdapter.CoinViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Coin>() {
            override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder = CoinViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
    )

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) = holder.bind(currentList[position])

    inner class CoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(coin: Coin) {
            itemView.apply {
                imageViewIcon.loadImage(coin.imageUrl)

                textViewName.text = if (coin.name.length < 8) coin.name else "${coin.name.subSequence(0, 7)}..."
                textViewCurrentPrice.text = "${coin.currentPrice.roundTo(2)}€"
                textViewMarketCapValue.text = "${coin.marketCap.formatNumber()}€"

                imageViewGrowthDrop.setImageResource(if (coin.priceChange < 0) R.drawable.ic_drop else R.drawable.ic_growth)

                textViewGrowthDrop.text = "${coin.priceChange.roundTo(2)}€ / ${coin.priceChangePercentage.roundTo(2)}%"

                setOnClickListener { onItemClicked(coin, imageViewIcon) }

                imageViewIcon.transitionName = coin.imageUrl
            }
        }
    }
}