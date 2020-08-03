package hr.ferit.matijasokol.coinmarket.ui.fragments.coins

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.db.CoinDatabase
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.models.Resource
import hr.ferit.matijasokol.coinmarket.other.*
import hr.ferit.matijasokol.coinmarket.other.Constants.RECYCLER_COLUMNS_NUMBER
import hr.ferit.matijasokol.coinmarket.repository.CoinMarketRepository
import hr.ferit.matijasokol.coinmarket.ui.adapters.CoinsAdapter
import hr.ferit.matijasokol.coinmarket.ui.coins.CoinsViewModel
import hr.ferit.matijasokol.coinmarket.ui.coins.CoinsViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_coins.*

class CoinsFragment : Fragment(R.layout.fragment_coins) {

    private val coinsAdapter by lazy { CoinsAdapter { coin, imageView -> onItemClicked(coin, imageView) } }
    private var firstRun = true

    private val repository by lazy { CoinMarketRepository(CoinDatabase(requireContext())) }
    private val viewModelProviderFactory by lazy { CoinsViewModelProviderFactory(requireActivity().application, repository) }
    private val viewModel by lazy { ViewModelProvider(this, viewModelProviderFactory).get(CoinsViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycler()
        observeChanges()
        setSwiper()

        if (firstRun) {
            viewModel.getCoins()
            firstRun = false
        }
    }

    private fun setSwiper() {
        swiper.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swiper.setOnRefreshListener {
            viewModel.getCoins()
        }
    }

    private fun observeChanges() {
        viewModel.coins.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    textViewList.gone()
                    response.data?.let {
                        coinsAdapter.submitList(it)
                        if (it.isEmpty()) {
                            textViewList.visible()
                        }
                    }

                    lottieList.gone()
                    closeSwiperIfRefreshing()
                }
                is Resource.Error -> {
                    lottieList.gone()
                    closeSwiperIfRefreshing()
                    response.message?.let {
                        rootLayout.showSnackbar(getString(R.string.error))
                    }
                }
                is Resource.Loading -> {
                    showAnimIfNeeded()
                }
            }
        })
    }

    private fun showAnimIfNeeded() {
        if (!swiper.isRefreshing) {
            lottieList.visible()
        }
    }

    private fun setRecycler() {
        recycler.apply {
            layoutManager = GridLayoutManager(requireContext(), RECYCLER_COLUMNS_NUMBER)
            adapter = coinsAdapter
            setHasFixedSize(true)

            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    private fun onItemClicked(coin: Coin, view: View) {
        if (!hasInternetConnection(requireContext())) {
            rootLayout.showSnackbar(getString(R.string.details_offline))
            return
        }

        view.transitionName = coin.imageUrl
        val extras = FragmentNavigatorExtras(
            view to coin.imageUrl
        )
        val action = CoinsFragmentDirections.actionCoinsFragmentToDetailsFragment(coin, coin.name)
        navigate(action, extras)
    }

    private fun navigate(destination: NavDirections, extraInfo: FragmentNavigator.Extras) =
        with(findNavController()) {
            currentDestination?.getAction(destination.actionId)
                ?.let {
                    navigate(destination, extraInfo)
                }
        }

    private fun closeSwiperIfRefreshing() {
        if (swiper.isRefreshing) {
            swiper.isRefreshing = false
        }
    }
}