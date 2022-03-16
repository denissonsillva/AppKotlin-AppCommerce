package br.com.djektech.appcommerce.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.com.djektech.appcommerce.model.OrderWithOrderedProducts
import br.com.djektech.appcommerce.repository.OrdersRepository

class OrderViewModel (application: Application) : AndroidViewModel(application) {

    private val ordersRepository = OrdersRepository(getApplication())

    fun getOrdersByUser(userId: String) = ordersRepository.loadAllByUser(userId)

    fun place(fullOrder: OrderWithOrderedProducts) = ordersRepository.place(fullOrder)

}