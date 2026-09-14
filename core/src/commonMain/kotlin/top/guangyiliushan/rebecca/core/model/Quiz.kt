package top.guangyiliushan.rebecca.core.model

import kotlinx.serialization.Serializable

/**
 * 练习会话模式（0.0.3 计划欠账，0.1.1 补落；路由参数与出题器共用）。
 *
 * ⚠ 消费面（review 🟡4）：0.1.1 补删除 Study 的 "More practice" 区块后，**UI 只有 MIXED 入口**
 * （LearningFocusCard 主 CTA）；LEARN/REVIEW 暂无任何可达入口，枚举与出题器保留不删。
 * 模式体系（学习/复习/混合的选择面）属 0.2.x 调度（SRS 0.2.2）一并接回，届时在此更新消费面。
 */
@Serializable
enum class QuizMode { LEARN, REVIEW, MIXED }
