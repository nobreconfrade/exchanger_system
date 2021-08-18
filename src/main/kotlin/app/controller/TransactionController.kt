package app.controller

import app.model.User
import java.util.concurrent.atomic.AtomicInteger

class TransactionController {
    val transactions = hashMapOf(
        0 to User(name = "Alice", email = "alice@alice.kt", id = 0),
        1 to User(name = "Bob", email = "bob@bob.kt", id = 1),
        2 to User(name = "Carol", email = "carol@carol.kt", id = 2),
        3 to User(name = "Dave", email = "dave@dave.kt", id = 3)
    )

    var lastId: AtomicInteger = AtomicInteger(transactions.size - 1)

//    fun save(name: String, email: String){
//        val id = lastId.incrementAndGet()
//        users.put(id, User(name = name, email = email, id = id))
//    }
//
//    fun findById(id: Int): User? {
//        return users[id]
//    }
//
//    fun findByEmail(email: String): User? {
//        return users.values.find { it.email == email }  // "it" is the same as {el -> el.email...}
//    }
//
//    fun update(id: Int, user: User){
//        users[id] = User(name = user.name, email = user.email, id = id)  // shouldn't break assigning to a val?
//    }
//
//    fun delete(id: Int){
//        users.remove(id)
//    }
}