package jp.s64.poc.cleanarchitecture.exception.app

import jp.s64.poc.cleanarchitecture.BoundaryException
import jp.s64.poc.cleanarchitecture.FeatureSpecificException
import java.lang.RuntimeException

sealed class AppLayerException(override val parent: Throwable?) : RuntimeException(parent), BoundaryException<Throwable> {
    class NotFound(parent: Throwable?) : AppLayerException(parent)
    class NetworkState(parent: Throwable?) : AppLayerException(parent)
    class PremiumMemberOnly(parent: Throwable?) : AppLayerException(parent)
    class FeatureSpecific(parent: Throwable?) : AppLayerException(parent), FeatureSpecificException<Throwable>
    class Unknown(parent: Throwable?) : AppLayerException(parent)
}
