package com.restugedepurnama.event.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity(
    @field:PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "summary")
    val summary: String,

    @field:ColumnInfo(name = "description")
    val description: String,

    @field:ColumnInfo(name = "imageLogo")
    val imageLogo: String,

    @field:ColumnInfo(name = "mediaCover")
    val mediaCover: String,

    @field:ColumnInfo(name = "category")
    val category: String,

    @field:ColumnInfo(name = "ownerName")
    val ownerName: String,

    @field:ColumnInfo(name = "cityName")
    val cityName: String,

    @field:ColumnInfo(name = "quota")
    val quota: Int,

    @field:ColumnInfo(name = "registrants")
    val registrants: Int,

    @field:ColumnInfo(name = "beginTime")
    val beginTime: String,

    @field:ColumnInfo(name = "endTime")
    val endTime: String,

    @field:ColumnInfo(name = "link")
    val link: String,

    @field:ColumnInfo(name = "favorite")
    var favorite: Boolean,

    @field:ColumnInfo(name = "isActive")
    val isActive: Boolean

)