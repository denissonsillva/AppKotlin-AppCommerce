package br.com.djektech.appcommerce.repository

import android.app.Application
import br.com.djektech.appcommerce.database.AppDatabase

class OrdersRepository (application: Application) {

    private val orderDao = AppDatabase.getDatabase(application).orderDao()

    fun loadAllByUser(userId: String) = orderDao.loadAllOrdersByUser(userId)

}