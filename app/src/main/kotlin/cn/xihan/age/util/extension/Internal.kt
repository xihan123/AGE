package cn.xihan.age.util.extension

internal const val NO_GETTER: String = "Property does not have a getter"

internal fun noGetter(): Nothing = throw NotImplementedError(NO_GETTER)
