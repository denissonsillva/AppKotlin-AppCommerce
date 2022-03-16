package br.com.djektech.appcommerce.database

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.djektech.appcommerce.model.User
import br.com.djektech.appcommerce.model.UserAddress
import br.com.djektech.appcommerce.model.UserWithAddresses

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    fun login(email: String, password: String) : User

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun loadUserById(userId: String) : LiveData<UserWithAddresses>

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Insert
    fun insert(userAddress: UserAddress)

    @Update
    fun update(userAddress: UserAddress)
}