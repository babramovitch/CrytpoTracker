package com.nebulights.coinstacks.Network.exchanges

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.CryptoTypes
import com.nebulights.coinstacks.userTicker

/**
 * Created by babramovitch on 2018-02-26.
 */
data class BasicAuthentication(
        var exchange: String,
        var apiKey: String,
        var apiSecret: String,
        var password: String,
        var userName: String,
        var cryptoTypes: List<String>) {


    fun getCryptoPairsMap(): MutableMap<CryptoTypes, CryptoPairs> {

        val cryptoPairMap: MutableMap<CryptoTypes, CryptoPairs> = mutableMapOf()

        cryptoTypes.forEach {
            val cryptoPair = CryptoPairs.valueOf(it)
            cryptoPairMap.put(cryptoPair.cryptoType, cryptoPair)
        }

        return cryptoPairMap
    }

    fun getUserTickers(): List<String> {

        val cryptoList: ArrayList<String> = arrayListOf()

        cryptoTypes.forEach {
            val cryptoPair = CryptoPairs.valueOf(it)
            cryptoList.add(cryptoPair.userTicker())
        }

        return cryptoList
    }

}


