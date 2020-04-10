package jp.s64.poc.cleanarchitecture.app.repository

import android.util.LruCache
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import jp.s64.poc.cleanarchitecture.IRepository
import jp.s64.poc.cleanarchitecture.app.entity.User
import jp.s64.poc.cleanarchitecture.app.entity.UserId
import jp.s64.poc.cleanarchitecture.app.service.ApiService
import jp.s64.poc.cleanarchitecture.exception.data.DataLayerException
import jp.s64.poc.cleanarchitecture.exception.transport.TransportLayerException
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger

class UserRepository(
    private val service: ApiService
) : IRepository {

    private val counter = AtomicInteger(0)
    private val inMemoryCache: LruCache<UserId, User> = LruCache(3)

    suspend fun findUser(id: UserId): Either<DataLayerException, User> {
        if (counter.addAndGet(1) % 3 == 0) {
            return Left(DataLayerException.FeatureSpecific(TriRequestException()))
        } else {
            return inMemoryCache.get(id)?.let { Right(it) } ?: service.user(id).also {
                it.map { inMemoryCache.put(id, it) }
            }.toRepositoryReturns()
        }
    }

    class TriRequestException : RuntimeException()

}

private fun <T> Either<TransportLayerException, T>.toRepositoryReturns(): Either<DataLayerException, T> {
    return this.mapLeft {
        when (it) {
            is TransportLayerException.NotFound -> DataLayerException.NotFound(it)
            is TransportLayerException.Forbidden -> DataLayerException.Forbidden(it)
            is TransportLayerException.BadRequest -> DataLayerException.IllegalOperation(it)
            is TransportLayerException.NotReachable -> DataLayerException.IllegalInfraState(it)
            else -> DataLayerException.Unknown(it)
        }
    }
}
