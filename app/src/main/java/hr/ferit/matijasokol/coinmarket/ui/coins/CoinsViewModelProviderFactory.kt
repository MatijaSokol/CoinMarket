package hr.ferit.matijasokol.coinmarket.ui.coins

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hr.ferit.matijasokol.coinmarket.repository.CoinMarketRepository

class CoinsViewModelProviderFactory(
    private val app: Application,
    private val repository: CoinMarketRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CoinsViewModel(
            app,
            repository
        ) as T
    }
}