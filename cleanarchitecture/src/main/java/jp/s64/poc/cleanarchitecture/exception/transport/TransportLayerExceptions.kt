package jp.s64.poc.cleanarchitecture.exception.transport

import jp.s64.poc.cleanarchitecture.BoundaryException
import jp.s64.poc.cleanarchitecture.FeatureSpecificException
import java.lang.RuntimeException

sealed class TransportLayerException(override val parent: Throwable?) : RuntimeException(parent), BoundaryException<Throwable> {
    class NotFound(parent: Throwable?) : TransportLayerException(parent)
    class Forbidden(parent: Throwable?) : TransportLayerException(parent)
    class NotReachable(parent: Throwable?) : TransportLayerException(parent)
    class BadRequest(parent: Throwable?) : TransportLayerException(parent)
    class FeatureSpecific(parent: Throwable?) : TransportLayerException(parent), FeatureSpecificException<Throwable>
    class Unknown(parent: Throwable?) : TransportLayerException(parent)
}
