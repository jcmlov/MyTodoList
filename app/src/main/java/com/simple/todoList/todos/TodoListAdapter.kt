package com.simple.todoList.todos

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.simple.todoList.R
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoListAdapter(val context: Context,
                      var itemList: MutableList<Todos>,
                      val setList: () -> Unit
) : RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_todo,parent,false)

        return TodoListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        val todo = itemList[position]

        holder.todoText.text = todo.todo
        holder.todoIsDone.isChecked = todo.isDone

        if (todo.isDone) {
            holder.todoText.apply {
                setTextColor(Color.GRAY)
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
            holder.todoText.apply {
                setTextColor(Color.BLACK)
                paintFlags = 0
                setTypeface(null, Typeface.NORMAL)
            }
        }

        holder.todoIsDone.apply {
            setOnClickListener {
                todo.isDone = this.isChecked
                setList()
            }
        }

        if (todo.time != null && todo.date != null) {
            holder.todoTime.apply {
                text = "${todo.date} ${todo.time}"
                visibility = View.VISIBLE
            }
        } else {
            holder.todoTime.apply {
                text = ""
                visibility = View.GONE
            }
        }

        holder.todoDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제") {str, dialogInterface ->
                    val todo = itemList[position]
                    setList()
                }
                .setNegativeButton("취소",null)
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class TodoListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val todoInfo = itemView.todo_info
        val todoText = itemView.todo_text
        val todoTime = itemView.todo_time
        //        val todoHashTag = itemView.todo_hash_tag  -- 사용하지 않음
        val todoDelete = itemView.todo_delete
        val todoIsDone: CheckBox = itemView.todo_done
    }

}