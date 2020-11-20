package com.legatotechnologies.v2.updater.datas.enums

enum class UpdateType(val type: Int) {

    FORCE(1),
    OPTIONAL(0);

    companion object {
        fun find(type: Int): UpdateType = values()
            .find { it.type == type } ?: OPTIONAL
    }
}