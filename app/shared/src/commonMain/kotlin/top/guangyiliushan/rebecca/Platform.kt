package top.guangyiliushan.rebecca

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform