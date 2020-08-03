package hr.ferit.matijasokol.coinmarket.ui.coins

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.db.CoinDatabase
import hr.ferit.matijasokol.coinmarket.repository.CoinMarketRepository

class CoinsActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(this, R.id.navHostFragment) }
    private val appBarConfiguration by lazy { AppBarConfiguration(navController.graph) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
