package br.com.djektech.appcommerce.model

import androidx.room.Embedded
import androidx.room.Entity
import java.io.Serializable
import java.util.*

@Entity(tableName = "ordered_products", primaryKeys = ["orderedProductId", "orderId"])
data class OrderedProduct (
    val orderedProductId: String = UUID.randomUUID().toString(),
    var orderId: String,
    @Embedded val product: Product,
    var size: String = "",
    var color: String = "",
    var quantity: Int = 0) : Serializable