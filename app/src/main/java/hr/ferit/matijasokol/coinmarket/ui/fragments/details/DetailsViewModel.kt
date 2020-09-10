package hr.ferit.matijasokol.coinmarket.ui.fragments.details

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.app.CoinMarketApplication
import hr.ferit.matijasokol.coinmarket.models.CoinInfoResponse
import hr.ferit.matijasokol.coinmarket.models.Resource
import hr.ferit.matijasokol.coinmarket.other.hasInternetConnection
import hr.ferit.matijasokol.coinmarket.repository.CoinMarketRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class DetailsViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: CoinMarketRepository
) : AndroidViewModel(app) {

    private val _lastDayCoinDetails = MutableLiveData<Resource<List<Float>>>()
    private val _yearCoinDetails = MutableLiveData<Resource<List<Float>>>()
    private val _coinInfo = MutableLiveData<Resource<CoinInfoResponse>>()

    val lastDayCoinDetails: LiveData<Resource<List<Float>>>
        get() = _lastDayCoinDetails

    val yearCoinDetails: LiveData<Resource<List<Float>>>
        get() = _yearCoinDetails

    val coinInfo: LiveData<Resource<CoinInfoResponse>>
        get() = _coinInfo

    fun getCoinDetails(id: String) = viewModelScope.launch(IO) {
        async {
            coinInfoCall(id)
            yearCoinDetailsCall(id)
            lastDayCoinDetailsCall(id)
        }
    }

    private suspend fun lastDayCoinDetailsCall(id: String) {
        try {
            if (hasInternetConnection(getApplication())) {
                _lastDayCoinDetails.postValue(Resource.Loading())
                val response = repository.getLastDayCoinDetails(id)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val lastDayData = resultResponse.prices.map { it[1] }.reversed().subList(0, 24).reversed()
                        _lastDayCoinDetails.postValue(Resource.Success(lastDayData))
                    }
                } else {
                    _lastDayCoinDetails.postValue(Resource.Error(response.message()))
                }
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _lastDayCoinDetails.postValue(
                    Resource.Error(getApplication<CoinMarketApplication>().getString(
                        R.string.network_failure)))
                else -> _lastDayCoinDetails.postValue(
                    Resource.Error(getApplication<CoinMarketApplication>().getString(
                        R.string.conversion_error)))
            }
        }
    }

    private suspend fun yearCoinDetailsCall(id: String) {
        try {
            if (hasInternetConnection(getApplication())) {
                _yearCoinDetails.postValue(Resource.Loading())
                val response = repository.getYearCoinDetails(id)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val yearDataInDays = resultResponse.prices.map { it[1] }
                        _yearCoinDetails.postValue(Resource.Success(yearDataInDays))
                    }
                } else {
                    _yearCoinDetails.postValue(Resource.Error(response.message()))
                }
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _yearCoinDetails.postValue(
                    Resource.Error(getApplication<CoinMarketApplication>().getString(
                        R.string.network_failure)))
                else -> _yearCoinDetails.postValue(
                    Resource.Error(getApplication<CoinMarketApplication>().getString(
                        R.string.conversion_error)))
            }
        }
    }

    private suspend fun coinInfoCall(id: String) {
        try {
            if (hasInternetConnection(getApplication())) {
                _coinInfo.postValue(Resource.Loading())
                val response = repository.getCoinInfo(id)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        _coinInfo.postValue(Resource.Success(resultResponse))
                    }
                } else {
                    _coinInfo.postValue(Resource.Error(response.message()))
                }
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _coinInfo.postValue(
                    Resource.Error(getApplication<CoinMarketApplication>().getString(
                        R.string.network_failure)))
                else -> _coinInfo.postValue(
                    Resource.Error(getApplication<CoinMarketApplication>().getString(
                        R.string.conversion_error)))
            }
        }
    }
}