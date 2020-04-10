package jp.s64.poc.cleanarchitecture.exception.data

import jp.s64.poc.cleanarchitecture.BoundaryException
import jp.s64.poc.cleanarchitecture.FeatureSpecificException
import java.lang.RuntimeException

sealed class DataLayerException(override val parent: Throwable?) : RuntimeException(parent), BoundaryException<Throwable> {
    class NotFound(parent: Throwable?) : DataLayerException(parent)
    class IllegalOperation(parent: Throwable?) : DataLayerException(parent)
    class Forbidden(parent: Throwable?) : DataLayerException(parent)
    class IllegalInfraState(parent: Throwable?) : DataLayerException(parent)
    class FeatureSpecific(parent: Throwable?) : DataLayerException(parent), FeatureSpecificException<Throwable>
    class Unknown(parent: Throwable?) : DataLayerException(parent)
}
