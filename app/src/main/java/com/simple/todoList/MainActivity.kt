package com.simple.todoList

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.simple.todoList.todos.Todo
import com.simple.todoList.todos.TodoViewModel
import com.simple.todoList.todos.Todos
import com.simple.todoList.todos.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    lateinit var todayAdapter : MyAdapter
    lateinit var viewModel : TodoViewModel
    lateinit var todoList: MutableLiveData<MutableList<Todo>>
    val itemList = arrayListOf<Todos>()    // 리스트 아이템 배열

    companion object {
        const val RC_GO_TO_DETAIL = 1004
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser
        var userId = currentUser?.uid
        /*
        val todos = db.collection("todoList")
        todos.get().addOnSuccessListener {
            if(it != null) {
                for (document in it.documents) {
                    document.getData()
                    val item = Todos(document.getData()?.get("userId").toString(), document.getData()?.get("todoList").toString(),
                                     document.getData()?.get("regDt").toString(), document.getData()?.get("isDone").toString().toBoolean())
                    itemList.add(item)
                }
            }
        }.addOnFailureListener{

        }
        */
        //뷰모델 받아오기
        viewModel = ViewModelProvider(this, ViewModelProviderFactory(this.application))
            .get(TodoViewModel::class.java)

        //recycler view에 보여질 아이템 Room에서 받아오기
        todoList = viewModel.mutableLiveData
        todoList.observe(this, Observer {
            todayAdapter.itemList = it
            todayAdapter.notifyDataSetChanged()
        })

        todayAdapter = MyAdapter(this, mutableListOf<Todo>(), viewModel, ::goToDetail, ::setList)

        //recycler view에 adapter와 layout manager 넣기
        today_list.adapter = todayAdapter
        today_list.layoutManager = LinearLayoutManager(this)


        todo_add.setOnClickListener {
            if (todo_input.text.toString() != "") {
                val todo = Todo(todo_input.text.toString())
                viewModel.insert(todo)
                // writeTodo(todo_input.text.toString(), userId.toString())
                setList()
                todo_input.setText("")
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun reload() {

    }

    private fun writeTodo(todoInput: String, userId: String) {
        var regDt : Long = System.currentTimeMillis()

        val todo = hashMapOf(
            "userId" to userId,
            "todoList" to todoInput,
            "isDone" to false,
            "regDt" to regDt
        )

        db.collection("todoList")
            .add(todo)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, getString(R.string.confirm_regist_success), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.confirm_regist_fail), Toast.LENGTH_SHORT).show()
            }
    }

    //메뉴 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            //등록일 기준 정렬
            R.id.menu_sort_register -> {
                viewModel.isTimeOrder = false
            }
            //날짜 기준 정렬
            R.id.menu_sort_date -> {
                viewModel.isTimeOrder = true
            }
            //완료 일괄 삭제
            R.id.menu_delete_done -> {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setMessage("완료된 할 일 목록을 전체 지우시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인") { _, _ ->
                        for (todo in todayAdapter.itemList) {
                            if (todo.isDone) {
                                viewModel.delete(todo)
                            }
                        }
                        setList()
                    }
                    .show()
            }
        }
        setList()
        return false
    }

    // RecyclerView의 item을 눌릴 때 상세페이지로 들어가지게끔 하는 함수. Adapter의 인자로 넣어줌.
    fun goToDetail(todo: Todo, position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("todoList", todo)
        intent.putExtra("data", bundle)
        intent.putExtra("position", position)
        startActivityForResult(intent, RC_GO_TO_DETAIL)
    }

    // DetailActivity에서 돌아온 이후의 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GO_TO_DETAIL && resultCode == Activity.RESULT_OK) {

            val bundle = data?.getBundleExtra("data")
            val todo = bundle?.getSerializable("todoList") as Todo
            viewModel.update(todo)
            setList()
        }
    }

    // 화면을 다시 돌리기 위해 viewModel 내에 있는 LiveData의 value를 변경시켜줌.
    // value가 변경됨에 따라 observer에 설정된 함수가 실행되고 UI가 변경됨.
    fun setList() {
        todoList.value = viewModel.getList(viewModel.isTimeOrder)
    }

}