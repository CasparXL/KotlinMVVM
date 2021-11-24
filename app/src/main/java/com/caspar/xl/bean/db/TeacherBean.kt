package com.caspar.xl.bean.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 *  @Create 2020/7/4.
 *  @Use
 */
@Entity(tableName = "teacher_table")
data class TeacherBean constructor(
    @PrimaryKey(autoGenerate = true)//主键自增
    var id: Long = 0,
    @ColumnInfo(name = "name")
    var name: String? = null,
    @ColumnInfo(name = "age")
    var age: Int = 0
) {
    @Ignore
    constructor() : this(0)
}