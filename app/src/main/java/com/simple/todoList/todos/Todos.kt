package com.simple.todoList.todos

data class Todos (
    val userId : String,
    val todo : String,
    val regDt : String,
    var time: String,
    var date: String,
    var dateLong: Long,
    var year: Int,
    var month: Int,
    var day: Int,
    var hour: Int,
    var minute: Int,
    var isDone : Boolean
)