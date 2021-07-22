package com.simple.todoList

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
import com.simple.todoList.todos.Todo
import com.simple.todoList.todos.TodoViewModel
import kotlinx.android.synthetic.main.item_todo.view.*

class MyAdapter(val context: Context,
                var itemList: MutableList<Todo>,
                val viewModel: TodoViewModel,
                val goToDetailListener : (Todo, Int) -> Unit,
                val setList: () -> Unit
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_todo,parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val todo = itemList[position]

        holder.todoText.text = todo.text
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
                viewModel.update(todo)
                setList()
            }
        }

        holder.todoInfo.setOnClickListener {
            goToDetailListener(todo, position)
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
                    viewModel.delete(todo)
                    setList()
                }
                .setNegativeButton("취소",null)
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val todoInfo = itemView.todo_info
        val todoText = itemView.todo_text
        val todoTime = itemView.todo_time
//        val todoHashTag = itemView.todo_hash_tag  -- 사용하지 않음
        val todoDelete = itemView.todo_delete
        val todoIsDone: CheckBox = itemView.todo_done
    }
}