package com.nebulights.coinstacks.Network.exchanges.Quadriga

import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.ExchangeNetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.exchanges.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.exchanges.Quadriga.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.security.HashGenerator
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.nebulights.coinstacks.Types.CryptoPairs
import io.reactivex.Observable
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by babramovitch on 10/25/2017.
 */

class QuadrigaRepository(private val service: QuadrigaService) : BaseExchange() {

    val maxQueriesPerMinute = 30

    override val userNameRequired: Boolean = true
    override val passwordRequired: Boolean = false
    override val userNameText = "Client ID"

    override fun feedType(): String = ExchangeProvider.QUADRIGACX_NAME

    override fun userNameText(): String {
        return userNameText
    }

    override fun startPriceFeed(
            tickers: List<CryptoPairs>,
            presenterCallback: NetworkCompletionCallback,
            exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate
    ) {
        clearTickerDisposables()
        addToPriceFeed(tickers, presenterCallback, exchangeNetworkDataUpdate)
    }

    override fun addToPriceFeed(
            tickers: List<CryptoPairs>,
            presenterCallback: NetworkCompletionCallback,
            exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {

        if (totalDisposables() > Constants.rateLimitSizeThreshold) {
            repeatDelayFromSize += 10000
        }

        launch {
            tickers.forEach { ticker ->
                startPriceFeed(
                        service.getCurrentTradingInfo(ticker.ticker),
                        repeatDelayFromSize,
                        ticker,
                        presenterCallback,
                        exchangeNetworkDataUpdate)

                delay(delayBetweenLoopCalls)
            }
        }
    }

    override fun startAccountFeed(
            basicAuthentication: BasicAuthentication,
            presenterCallback: NetworkCompletionCallback,
            exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {

        super.startAccountBalanceFeed(
                Observable
                        .defer<AuthenticationDetails> {
                            Observable.just(
                                    generateAuthenticationDetails(basicAuthentication)
                            )
                        }
                        .flatMap<Any> { balances -> service.getBalances(balances) },
                basicAuthentication,
                presenterCallback,
                exchangeNetworkDataUpdate)
    }

    override fun validateApiKeys(
            basicAuthentication: BasicAuthentication,
            presenterCallback: ValidationCallback,
            exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {

        super.validateAPiKeys(
                service.getBalances(generateAuthenticationDetails(basicAuthentication)),
                basicAuthentication,
                presenterCallback,
                exchangeNetworkDataUpdate)
    }


    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {

        val nonce = System.currentTimeMillis()
        val clientId = basicAuthentication.userName
        val secret = basicAuthentication.apiSecret

        val key = basicAuthentication.apiKey

        val mergedString = nonce.toString() + clientId + key

        val signature = HashGenerator.generateHmacDigest(
                mergedString.toByteArray(), secret.toByteArray(),
                HashingAlgorithms.HmacSHA256
        )

        return AuthenticationDetails(key, signature, nonce)
    }
}
