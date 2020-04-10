package jp.s64.poc.cleanarchitecture.app.datasource

import jp.s64.poc.cleanarchitecture.app.entity.Email
//import jp.s64.poc.cleanarchitecture.app.entity.ResourceId
import jp.s64.poc.cleanarchitecture.app.entity.User
import jp.s64.poc.cleanarchitecture.app.entity.UserId
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

typealias PageNum = ULong
typealias ItemCount = ULong
//typealias Token = String

/**
 * https://reqres.in
 */
interface DummyApi {

    @GET("users/{id}")
    suspend fun user(@Path("id") id: UserId): Response<UserResponse>

    @GET("users")
    suspend fun users(): Response<UsersResponse>

    /*
    @GET("unknown/{id}")
    suspend fun resource(@Path("id") id: ResourceId)

    @POST("login")
    suspend fun login(req: LoginRequest): Response<LoginResponse>
    */

    interface GetResponse<T> {
        val data: T
        val ad: Any
    }

    interface GetListResponse<T> : GetResponse<T> where T : List<*> {
        val page: PageNum
        val perPage: ItemCount
        val total: ItemCount
        val totalPages: PageNum
    }

    data class UserResponse(
        override val data: User,
        override val ad: Any
    ) : GetResponse<User>

    data class UsersResponse(
        override val data: List<User>,
        override val ad: Any,
        override val page: PageNum,
        override val perPage: ItemCount,
        override val total: ItemCount,
        override val totalPages: PageNum
    ) : GetListResponse<List<User>>

    /*
    data class LoginRequest(
        val email: Email?,
        val password: String?
    )

    data class LoginResponse(
        val token: Token?,
        val error: String?
    )
    */

}
