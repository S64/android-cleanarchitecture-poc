package jp.s64.poc.cleanarchitecture

/**
 * Marker I/F
 */
interface IUseCase

/**
 * Marker I/F
 */
interface IService

/**
 * Marker I/F
 */
interface IInteractor

/**
 * Marker I/F
 */
interface IRepository

/**
 * Marker I/F
 */
interface IPresenter

/**
 * Marker I/F
 */
interface BoundaryException<T> where T : Throwable {
    val parent: T?
}

/**
 * Marker I/F
 */
interface FeatureSpecificException<T> : BoundaryException<T> where T : Throwable
