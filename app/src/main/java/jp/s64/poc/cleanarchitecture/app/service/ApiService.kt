package jp.s64.poc.cleanarchitecture.app.service

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jp.s64.poc.cleanarchitecture.app.datasource.DummyApi
import jp.s64.poc.cleanarchitecture.app.entity.User
import jp.s64.poc.cleanarchitecture.app.entity.UserId
import jp.s64.poc.cleanarchitecture.exception.transport.TransportLayerException
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.UnknownHostException

class ApiService {

    private val client: DummyApi

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Endpoint)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        client = retrofit.create(DummyApi::class.java)
    }

    suspend fun user(id: UserId): Either<TransportLayerException, User> {
        return runApi { client.user(id) }.map { it.data }
    }

    suspend fun users(): Either<TransportLayerException, List<User>> {
        return runApi { client.users() }.map { it.data }
    }

    companion object {

        private const val Endpoint = "https://reqres.in/api/"

    }

}

private suspend fun <T> runApi(
    block: suspend () -> Response<T>
): Either<TransportLayerException, T> {
    try {
        delay(200)
        return block().toServiceReturns()
    } catch (ex: Throwable) {
        return Left(
            when (ex) {
                is UnknownHostException -> TransportLayerException.NotReachable(ex)
                else -> TransportLayerException.Unknown(ex)
            }
        )
    }
}

private fun <T> Response<T>.toServiceReturns(): Either<TransportLayerException, T> {
    return when {
        isSuccessful -> Right(this.body()!!)
        else -> when (code()) {
            404 -> Left(TransportLayerException.NotFound(null))
            else -> Left(TransportLayerException.NotReachable(null))
        }
    }
}
