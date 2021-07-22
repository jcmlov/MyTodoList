package com.simple.todoList.todos

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simple.todoList.todos.Todo
import com.simple.todoList.todos.TodoDao

@Database(entities = [Todo::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}