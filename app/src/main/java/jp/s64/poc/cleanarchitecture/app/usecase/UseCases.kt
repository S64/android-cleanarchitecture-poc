package jp.s64.poc.cleanarchitecture.app.usecase

import arrow.core.Either
import jp.s64.poc.cleanarchitecture.IUseCase
import jp.s64.poc.cleanarchitecture.app.entity.User
import jp.s64.poc.cleanarchitecture.app.entity.UserId
import jp.s64.poc.cleanarchitecture.exception.app.AppLayerException
import java.lang.RuntimeException

interface GetUserUseCase : IUseCase {

    suspend fun getUser(id: UserId?): Either<AppLayerException, User>

    class MyUserLimitException(cause: Throwable) : RuntimeException(cause)
    class MyEmptyUserIdException : RuntimeException()

}
