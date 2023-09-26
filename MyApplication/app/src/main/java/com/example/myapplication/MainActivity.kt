package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.*
import androidx.room.*
import androidx.room.Room.databaseBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var db: AppDatabase? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = databaseBuilder(applicationContext, AppDatabase::class.java, "dbdb").build()
        setContentView(R.layout.activity_main)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onSaveClicked(view: View) {
        val name = findViewById<EditText>(R.id.editTextName)
        val surname = findViewById<EditText>(R.id.editTextSurname)
        if(name.text.isNotEmpty() and surname.text.isNotEmpty()){
            GlobalScope.launch {
                db!!.userDao()?.insert(User(null, name = name.text.toString(), surname = surname.text.toString() ))
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun onRefreshClicked(view: View) {
        GlobalScope.launch {
            val vivi = findViewById<TextView>(R.id.revView)
            var txt = ""
            for (elem in db!!.userDao()!!.getAll()){
                txt += "id:${elem!!.id.toString()}| name:${elem.name}| surname:${elem.surname}\n"
            }
            vivi.text = txt
        }
    }

    @Entity(tableName = "users")
    data class User(
        @PrimaryKey(autoGenerate = true) val id: Long?,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "surname") val surname: String
    )

    @Dao
    interface UserDao {
        @Query("SELECT * FROM users") // вот этот запрос фактически выполняется
        suspend fun getAll(): List<User?> // список чуваков получаем

        @Query("SELECT * FROM users WHERE id = :id")
        fun getById(id: Long): User // берем чувака по ID

        @Insert
        suspend fun insert(user: User) // добавляем чувака (обьектом таблицы)

        @Update
        suspend fun update(user: User) // меняем инфу о чуваке
    }


    @Database(entities = [User::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun userDao(): UserDao?
    }
}