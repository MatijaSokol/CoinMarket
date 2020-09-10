package hr.ferit.matijasokol.coinmarket.ui.coins

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.app.CoinMarketApplication
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.models.Resource
import hr.ferit.matijasokol.coinmarket.other.hasInternetConnection
import hr.ferit.matijasokol.coinmarket.repository.CoinMarketRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException

class CoinsViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: CoinMarketRepository
) : AndroidViewModel(app) {

    private val _coins = MutableLiveData<Resource<List<Coin>>>()

    val coins: LiveData<Resource<List<Coin>>>
        get() = _coins

    fun getCoins() = viewModelScope.launch(IO) {
        coinsCall()
    }

    private suspend fun coinsCall() {
        try {
            if (hasInternetConnection(getApplication())) {
                _coins.postValue(Resource.Loading())
                val response = repository.getCoins()
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        _coins.postValue(Resource.Success(resultResponse))
                        repository.upsertList(resultResponse)
                    }
                } else {
                    _coins.postValue(Resource.Error(response.message()))
                }
            } else {
                val savedCoins = repository.getSavedCoins()
                _coins.postValue(Resource.Success(savedCoins))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _coins.postValue(Resource.Error(getApplication<CoinMarketApplication>().getString(R.string.network_failure)))
                else -> _coins.postValue(Resource.Error(getApplication<CoinMarketApplication>().getString(R.string.conversion_error)))
            }
        }
    }
}