package top.guangyiliushan.rebecca.core.demo

import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.AffixKind
import top.guangyiliushan.rebecca.core.model.Book
import top.guangyiliushan.rebecca.core.model.ConfusablePair
import top.guangyiliushan.rebecca.core.model.Lemma
import top.guangyiliushan.rebecca.core.model.RootAffix
import top.guangyiliushan.rebecca.core.model.Sense
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.WordOfTheDay

internal val DEMO_ACCOUNT = AccountId("demo-account")
internal val DEMO_NOW = Instant.fromEpochMilliseconds(1_700_000_000_000L)

/**
 * 种子数据唯一真源。构成约束（plan Q6 / R2 证据链 15-20 lemma / 20-30 sense）：
 * - 16 lemma / 28 sense；
 * - run 同词性(verb) 4 义项（quiz 需 3 个可信干扰项，Haladyna & Rodriguez 2013）；
 * - ≥2 组共享词根词族：port（port/import/export/transport）、form（form/reform/transform/uniform），search 前缀匹配不失真；
 * - 多义项 lemma ≥5（run×4、import×2、reform×2、perform×2、review×2、book×2、port×2、light×3）。
 */
internal val DEMO_LEMMAS = listOf(
    Lemma("lemma-run", "run"),
    Lemma("lemma-port", "port"),
    Lemma("lemma-form", "form"),
    Lemma("lemma-supply", "supply"),
    Lemma("lemma-book", "book"),
    Lemma("lemma-light", "light"),
    Lemma("lemma-transport", "transport"),
    Lemma("lemma-import", "import"),
    Lemma("lemma-export", "export"),
    Lemma("lemma-reform", "reform"),
    Lemma("lemma-inform", "inform"),
    Lemma("lemma-perform", "perform"),
    Lemma("lemma-uniform", "uniform"),
    Lemma("lemma-transform", "transform"),
    Lemma("lemma-overflow", "overflow"),
    Lemma("lemma-review", "review"),
)

internal val DEMO_SENSES = listOf(
    // run：同词性(verb) 4 义项（quiz 干扰项约束）
    Sense(
        SenseId("sense-run-1"), "lemma-run", "move at a speed faster than a walk", "verb",
        ipa = "/rʌn/", examples = listOf("He runs every morning."), wordFamily = listOf("runner", "running", "rerun"),
    ),
    Sense(SenseId("sense-run-2"), "lemma-run", "operate or function", "verb", ipa = "/rʌn/"),
    Sense(SenseId("sense-run-3"), "lemma-run", "manage or direct", "verb", ipa = "/rʌn/"),
    Sense(SenseId("sense-run-4"), "lemma-run", "compete in a race", "verb", ipa = "/rʌn/", examples = listOf("She is running in the marathon.")),
    // port：共享词根 port=carry（词族约束）
    Sense(
        SenseId("sense-port-1"), "lemma-port", "a town or place where ships load and unload", "noun",
        ipa = "/pɔːrt/", examples = listOf("The ship arrived at the port."), wordFamily = listOf("import", "export", "transport"),
    ),
    Sense(SenseId("sense-port-2"), "lemma-port", "the left side of a ship", "noun", ipa = "/pɔːrt/"),
    // form
    Sense(
        SenseId("sense-form-1"), "lemma-form", "the shape or structure of something", "noun",
        ipa = "/fɔːrm/", examples = listOf("Water exists in three forms."), wordFamily = listOf("formal", "formation", "reform"),
    ),
    Sense(SenseId("sense-form-2"), "lemma-form", "to make or shape", "verb", ipa = "/fɔːrm/"),
    // supply
    Sense(
        SenseId("sense-supply-1"), "lemma-supply", "make something needed available", "verb",
        ipa = "/səˈplaɪ/", examples = listOf("The school supplies books."), wordFamily = listOf("supplier", "supply"),
    ),
    // book
    Sense(
        SenseId("sense-book-1"), "lemma-book", "a set of printed pages bound together", "noun",
        ipa = "/bʊk/", examples = listOf("She reads a book every week."), wordFamily = listOf("booklet", "notebook"),
    ),
    Sense(SenseId("sense-book-2"), "lemma-book", "to arrange in advance", "verb", ipa = "/bʊk/"),
    // light
    Sense(
        SenseId("sense-light-1"), "lemma-light", "the brightness that lets us see things", "noun",
        ipa = "/laɪt/", examples = listOf("The light was too bright."), wordFamily = listOf("lighter", "enlighten"),
    ),
    Sense(SenseId("sense-light-2"), "lemma-light", "not heavy", "adjective", ipa = "/laɪt/"),
    Sense(SenseId("sense-light-3"), "lemma-light", "to make brighter; to begin to burn", "verb", ipa = "/laɪt/"),
    // transport（port 词根族）
    Sense(
        SenseId("sense-transport-1"), "lemma-transport", "to carry goods or people from one place to another", "verb",
        ipa = "/ˈtrænspɔːrt/", examples = listOf("The goods were transported by train."), wordFamily = listOf("transportation"),
    ),
    // import（port 词根族；多义项）
    Sense(
        SenseId("sense-import-1"), "lemma-import", "to bring goods into a country from abroad", "verb",
        ipa = "/ɪmˈpɔːrt/", examples = listOf("They import coffee from Brazil."), wordFamily = listOf("importer"),
    ),
    Sense(SenseId("sense-import-2"), "lemma-import", "something brought in from another country", "noun", ipa = "/ˈɪmpɔːrt/"),
    // export（port 词根族）
    Sense(
        SenseId("sense-export-1"), "lemma-export", "to send goods to another country to sell", "verb",
        ipa = "/ɪkˈspɔːrt/", wordFamily = listOf("exporter"),
    ),
    // reform（form 词根族；多义项）
    Sense(
        SenseId("sense-reform-1"), "lemma-reform", "to make changes to improve something", "verb",
        ipa = "/rɪˈfɔːrm/", examples = listOf("They reformed the tax system."), wordFamily = listOf("reformer"),
    ),
    Sense(SenseId("sense-reform-2"), "lemma-reform", "a change that improves something", "noun", ipa = "/rɪˈfɔːrm/"),
    // inform（form 词根族）
    Sense(
        SenseId("sense-inform-1"), "lemma-inform", "to tell someone about something", "verb",
        ipa = "/ɪnˈfɔːrm/", wordFamily = listOf("information", "informative"),
    ),
    // perform（form 词根族；多义项）
    Sense(
        SenseId("sense-perform-1"), "lemma-perform", "to do an action or task", "verb",
        ipa = "/pərˈfɔːrm/", wordFamily = listOf("performance"),
    ),
    Sense(SenseId("sense-perform-2"), "lemma-perform", "to entertain an audience", "verb", ipa = "/pərˈfɔːrm/"),
    // uniform（form 词根族）
    Sense(SenseId("sense-uniform-1"), "lemma-uniform", "special clothing worn by members of a group", "noun", ipa = "/ˈjuːnɪfɔːrm/"),
    // transform（form 词根族）
    Sense(
        SenseId("sense-transform-1"), "lemma-transform", "to change completely in form or appearance", "verb",
        ipa = "/trænsˈfɔːrm/", wordFamily = listOf("transformation"),
    ),
    // overflow
    Sense(SenseId("sense-overflow-1"), "lemma-overflow", "to flow over the edge of a container", "verb", ipa = "/ˌoʊvərˈfloʊ/"),
    // review（多义项）
    Sense(
        SenseId("sense-review-1"), "lemma-review", "to look at or consider again", "verb",
        ipa = "/rɪˈvjuː/", wordFamily = listOf("reviewer"),
    ),
    Sense(SenseId("sense-review-2"), "lemma-review", "a report giving an opinion about something", "noun", ipa = "/ˈrevjuː/"),
)

internal val DEMO_BOOKS = listOf(
    Book(
        "book-1", "A Morning in the Park", "Demo Author",
        listOf(
            "Tom woke up early. He walked to the park. The sun was bright.",
            "Birds were singing in the trees. He bought a cup of coffee.",
            "It was delicious. On the way home, he began to run. It started to rain.",
        ),
    ),
    Book(
        "book-2", "The Little Bookshop", "Demo Author",
        listOf(
            "Anna booked a table at the cafe near the bookshop.",
            "She read her favorite book. The light of the lamp was warm.",
            "She decided to form a reading club with her friends.",
        ),
    ),
)

internal val DEMO_WOTD = WordOfTheDay(SenseId("sense-run-1"), "move fast: he runs every morning")

internal val DEMO_CONFUSABLES = listOf(
    ConfusablePair(SenseId("sense-run-2"), SenseId("sense-run-3"), "run: operate(机器运转) vs manage(经营管理)"),
    ConfusablePair(SenseId("sense-port-1"), SenseId("sense-port-2"), "port: 港口 vs 船的左舷"),
    ConfusablePair(SenseId("sense-light-2"), SenseId("sense-light-3"), "light: 轻的(adj) vs 点燃(v)"),
    ConfusablePair(SenseId("sense-import-1"), SenseId("sense-export-1"), "import: 进口(运进来) vs export: 出口(运出去)"),
)

internal val DEMO_ROOTS = listOf(
    RootAffix("port", "carry（搬运）", AffixKind.ROOT),
    RootAffix("form", "shape（形状）", AffixKind.ROOT),
    RootAffix("re-", "again, back（再、回）", AffixKind.PREFIX),
    RootAffix("im-/in-", "into（进入）", AffixKind.PREFIX),
    RootAffix("ex-", "out（向外）", AffixKind.PREFIX),
    RootAffix("trans-", "across（穿过）", AffixKind.PREFIX),
    RootAffix("-er", "one who does（做…的人）", AffixKind.SUFFIX),
)
