package jp.s64.poc.cleanarchitecture.app.interactor

import arrow.core.Either
import arrow.core.Left
import jp.s64.poc.cleanarchitecture.FeatureSpecificException
import jp.s64.poc.cleanarchitecture.IInteractor
import jp.s64.poc.cleanarchitecture.app.entity.User
import jp.s64.poc.cleanarchitecture.app.entity.UserId
import jp.s64.poc.cleanarchitecture.app.repository.UserRepository
import jp.s64.poc.cleanarchitecture.app.usecase.GetUserUseCase
import jp.s64.poc.cleanarchitecture.exception.app.AppLayerException
import jp.s64.poc.cleanarchitecture.exception.data.DataLayerException
import jp.s64.poc.cleanarchitecture.exception.transport.TransportLayerException
import kotlinx.coroutines.delay

class UserInteractor(
    private val repository: UserRepository
) : GetUserUseCase, IInteractor {

    override suspend fun getUser(id: UserId?): Either<AppLayerException, User> {
        if (id == null) {
            return Left(
                AppLayerException.FeatureSpecific(GetUserUseCase.MyEmptyUserIdException())
            )
        }
        delay(50)
        return repository.findUser(id).toUseCaseReturns {
            when (val e = it.parent) {
                is UserRepository.TriRequestException
                    -> AppLayerException.FeatureSpecific(GetUserUseCase.MyUserLimitException(e))
                else -> AppLayerException.Unknown(it)
            }
        }
    }

}

private fun <T> Either<DataLayerException, T>.toUseCaseReturns(
    block: (exception: DataLayerException.FeatureSpecific) -> AppLayerException = { AppLayerException.Unknown(it) }
): Either<AppLayerException, T> {
    return this.mapLeft {
        when (it) {
            is DataLayerException.NotFound -> AppLayerException.NotFound(it)
            is DataLayerException.Forbidden -> AppLayerException.PremiumMemberOnly(it)
            is DataLayerException.IllegalInfraState -> when (it.parent) {
                is TransportLayerException.NotReachable -> AppLayerException.NetworkState(it)
                else -> AppLayerException.Unknown(it)
            }
            is DataLayerException.FeatureSpecific -> block(it)
            else -> AppLayerException.Unknown(it)
        }
    }
}
