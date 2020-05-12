package com.caspar.xl.bean.db

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
@Entity
class UserBean(
    @Id var id: Long = 0,
    var name: String? = null,
    var age: Int = 0
)