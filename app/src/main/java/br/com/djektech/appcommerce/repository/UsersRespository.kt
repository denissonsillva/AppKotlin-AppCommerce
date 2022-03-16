package br.com.djektech.appcommerce.repository

import android.app.Application
import br.com.djektech.appcommerce.database.AppDatabase
import br.com.djektech.appcommerce.model.User
import br.com.djektech.appcommerce.model.UserAddress

class UsersRepository (application: Application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun login(email: String, password: String) = userDao.login(email, password)

    fun loadWithAddresses(userID: String) = userDao.loadUserById(userID)

    fun insert(user: User) = userDao.insert(user)

    fun insert(userAddress: UserAddress) = userDao.insert(userAddress)

    fun update(user: User) = userDao.update(user)

    fun update(userAddress: UserAddress) = userDao.update(userAddress)

}