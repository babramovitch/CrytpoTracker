package com.nebulights.crytpotracker.Network

import com.nebulights.crytpotracker.Network.Quadriga.QuadrigaRepository
import com.nebulights.crytpotracker.Network.Quadriga.QuadrigaService

/**
 * Created by babramovitch on 10/25/2017.
 */
object RepositoryProvider {
    fun provideQuadrigaRepository(): QuadrigaRepository {
        return QuadrigaRepository(QuadrigaService.create())
    }
}