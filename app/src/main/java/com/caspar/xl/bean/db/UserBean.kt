package com.caspar.xl.bean.db

import androidx.room.*


/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:用户信息
 */
@Entity(
    tableName = "user_table",
    foreignKeys = [ForeignKey(
        entity = TeacherBean::class,
        parentColumns = ["id"],
        childColumns = ["t_id"]
    )]
)
data class UserBean constructor(
    @PrimaryKey(autoGenerate = true)//主键自增
    var id: Long = 0,
    @ColumnInfo(name = "name")
    var name: String = "",
    @ColumnInfo(name = "age")
    var age: Int = 0,
    @ColumnInfo(name = "t_id", index = true)
    var tId: Long = 0
) {
    @Ignore
    constructor() : this(0)
}