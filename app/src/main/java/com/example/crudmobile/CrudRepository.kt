package com.example.crudmobile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

class CrudRepository {

    lateinit var retrofit: Retrofit
    lateinit var productsApi: ProductsApi

    init {
        retrofit =
            Retrofit
                .Builder()
                // Configura a URL do servidor
                .baseUrl("http://192.168.1.148:8080")
                // Adiciona a biblioteca de conversão para JSON
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        productsApi = retrofit.create(ProductsApi::class.java)
        // TODO criar client api
    }

    suspend fun getProducts(): List<Product> {
        return productsApi.getProducts()
    }

    fun createProduct(product: Product) {
        productsApi.createProduct(product)
    }

    fun updateProduct(id: Int, product: Product) {
        productsApi.updateProduct(id, product)
    }

    fun deleteProduct(id: Int) {
        productsApi.deleteProduct(id)
    }
}

// Mapeamento dos endpoints da API
interface ProductsApi {
    @GET("/products")
    suspend fun getProducts(): List<Product>

    @POST("/new-product")
    fun createProduct(product: Product)

    @PUT("/update-product/{id}")
    fun updateProduct(id: Int, product: Product)

    @DELETE("/remove-product/{id}")
    fun deleteProduct(id: Int)
}

// Mapeamento do objeto (Precisa estar igual na API backend)
data class Product(
    val id: Int,
    val name: String,
    val price: String,
)