package top.guangyiliushan.rebecca.core.model

import kotlinx.serialization.Serializable

/** 练习会话模式（0.0.3 计划欠账，0.1.1 补落；路由参数与出题器共用）。 */
@Serializable
enum class QuizMode { LEARN, REVIEW, MIXED }
