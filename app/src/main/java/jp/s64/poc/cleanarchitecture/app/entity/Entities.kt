package jp.s64.poc.cleanarchitecture.app.entity

import com.squareup.moshi.Json

typealias UserId = Long
typealias Email = String
typealias AvatarUrl = String

data class User(
    @Json(name = "id")
    val id: UserId,
    @Json(name = "email")
    val email: Email,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
    @Json(name = "avatar")
    val avatar: AvatarUrl
)


/*
typealias ResourceId = Long

data class Resource(
    val id: ResourceId
)
*/
