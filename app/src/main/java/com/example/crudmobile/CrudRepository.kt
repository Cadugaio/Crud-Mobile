package com.example.crudmobile

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

class CrudRepository {

    lateinit var retrofit: Retrofit
    lateinit var productsApi: ProductsApi
    lateinit var clientsApi: ClientsApi

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
        clientsApi = retrofit.create(ClientsApi::class.java)
    }

    suspend fun getClients(): List<Client> {
        return clientsApi.getClients()
    }

    suspend fun createClient(client: Client) {
        clientsApi.createClient(client)
    }

    suspend fun updateClient(id: Int, name: String) {
        clientsApi.updateClient(id, name)
    }

    suspend fun deleteClient(id: Int) {
        clientsApi.deleteClient(id)
    }

    suspend fun getProducts(): List<Product> {
        return productsApi.getProducts()
    }

    suspend fun createProduct(product: Product) {
        productsApi.createProduct(product)
    }

    suspend fun updateProduct(id: Int, product: Product) {
        productsApi.updateProduct(id, product)
    }

    suspend fun deleteProduct(id: Int) {
        productsApi.deleteProduct(id)
    }
}

interface ClientsApi {
    @GET("/clients")
    suspend fun getClients(): List<Client>

    @POST("/new-client")
    suspend fun createClient(
        @Body client: Client
    )

    @PUT("/update-client/{id}")
    suspend fun updateClient(
        @Path("id") id: Int,
        @Body name: String
    )

    @DELETE("/remove-client/{id}")
    suspend fun deleteClient(
        @Path("id") id: Int
    )
}

// Mapeamento dos endpoints da API
interface ProductsApi {
    @GET("/products")
    suspend fun getProducts(): List<Product>

    @POST("/new-product")
    suspend fun createProduct(
        @Body product: Product
    )

    @PUT("/update-product/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    )

    @DELETE("/remove-product/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int)
}

// Mapeamento do objeto (Precisa estar igual na API backend)
data class Product(
    val id: Int,
    val name: String,
    val price: String,
)

data class Client(
    val id: Int,
    val name: String,
)


