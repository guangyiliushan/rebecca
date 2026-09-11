package top.guangyiliushan.rebecca.core.model

import kotlin.time.Instant

/** 偏好键值；valueJson 是序列化载荷（0.0.4 用 String，0.4.1 接 kotlinx.serialization 后类型化）。 */
data class Preference(
    val accountId: AccountId,
    val key: String,
    val valueJson: String,
    val updatedAt: Instant,
)
