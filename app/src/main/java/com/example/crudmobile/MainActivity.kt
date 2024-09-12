package com.example.crudmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crudmobile.ui.theme.CrudmobileTheme
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // Inicialização do repositório, chama o init da classe CrudRepository
    val crudRepository = CrudRepository()

    // Cria a lista de produtos vazia inicialmente
    val products = MutableLiveData<List<Product>>()

    val clients = MutableLiveData<List<Client>>()

    init {
        // Dispatchers.IO faz a comunicação com o servidor em uma thread separada para evitar
        // travamentos na tela (busca os dados em segundo plano)
        viewModelScope.launch(Dispatchers.IO) {
            // Busca os produtos do repositório
            updateProducts()
        }

        updateClientsList()
    }

    fun updateProductsList() {
        // Dispatchers.IO faz a comunicação com o servidor em uma thread separada para evitar
        // travamentos na tela (busca os dados em segundo plano)
        viewModelScope.launch(Dispatchers.IO) {
            // Busca os produtos do repositório
            updateProducts()
        }
    }

    fun createProduct(name: String, price: String) {
        viewModelScope.launch(Dispatchers.IO) {
            crudRepository.createProduct(
                Product(
                    id = products.value!!.size + 1,
                    name = name,
                    price = price,
                )
            )
            updateProducts()
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            crudRepository.deleteProduct(
                id
            )
            updateProducts()
        }

    }

    private suspend fun updateProducts() {
        crudRepository.getProducts().let {
            // Atualiza a variavel de produtos com os dados retornados do servidor
            products.postValue(it)
        }
    }

    fun updateClientsList() {
        viewModelScope.launch(Dispatchers.IO) {
            crudRepository.getClients().let {
                clients.postValue(it)
            }
        }
    }

}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CrudmobileTheme {
                // A surface container using the 'background' color from the theme
                val mainVM: MainViewModel = viewModel()
                // Busca a lista de produtos do view model e observa todas as alterações
                val productsList = mainVM.products.observeAsState()
                val clientsList = mainVM.clients.observeAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        // Envia a lista de produtos para ser desenhado na UI
                        products = productsList.value ?: emptyList(),
                        updateProductsList = { mainVM.updateProductsList() },
                        createProduct = { name, price -> mainVM.createProduct(name, price) },
                        deleteProduct = { id -> mainVM.deleteProduct(id) },
                        clients = clientsList.value ?: emptyList(),
                        updateClientsList = { mainVM.updateClientsList() },

                        )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    products: List<Product>,
    updateProductsList: () -> Unit,
    deleteProduct: (Int) -> Unit,
    createProduct: (String, String) -> Unit,
    clients: List<Client>,
    updateClientsList: () -> Unit,
) {
    // Implementação da UI do aplicativo
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "Products list",
            textAlign = TextAlign.Center,
        )
        // Organiza os dados em lista
        LazyColumn {
            items(products) {
                // Cria uma linha para cada produto da lista
                // it = a cada produto da lista separado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = it.name)
                    Text(text = it.price)
                    Button(onClick = { deleteProduct(it.id) }) {
                        Text(text = "Delete")

                    }
                }
            }

        }

        var productName = remember { mutableStateOf("") }
        var productPrice = remember { mutableStateOf("") }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                value = productName.value,
                onValueChange = { productName.value = it },
            )
            BasicTextField(
                value = productPrice.value,
                onValueChange = { productPrice.value = it }
            )
            Button(
                onClick = {
                    createProduct(productName.value, productPrice.value)
                    productName.value = ""
                    productPrice.value = ""
                }
            ) {
                Text(text = "+")
            }
        }

        Button(onClick = { updateProductsList() }) {
            Text(text = "Update")
        }


        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "Client list",
            textAlign = TextAlign.Center,
        )

        if (clients.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "no client found",
                textAlign = TextAlign.Center
            )

        }

        LazyColumn {
            items(clients) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = it.name)
                }
            }
        }
        Button(onClick = { updateClientsList() }) {
            Text(text = "Update clients")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CrudmobileTheme {
        MainScreen(
            products = listOf(
                Product(1, "Teste", "R$ 123"),
                Product(2, "Teste1", "R$ 123"),
                Product(3, "Teste2", "R$ 123"),
                Product(4, "Teste3", "R$ 123"),
            ),
            clients = listOf(
                Client(id = 11, "client 1"),
                Client(id = 12, "client 2"),
                Client(id = 13, "client 3")
            ),
            updateClientsList = {},
            updateProductsList = {},
            createProduct = { name, price -> },
            deleteProduct = {},
        )
    }
}


// todo message not found products

// CREATE CLIENT
// No MainScreen
// TODO adicionar um row para o campo de texto e o botão de criar cliente
// TODO criar variavel para nome do cliente (variavelDoNomeDoCliente)
// TODO criar o campo de texto para o nome do cliente (BasicTextField)
// TODO criar botão para criar cliente
// TODO criar a função createClient(name: String) como parametro do MainScreen()
// TODO chamar a função createClient(variavelDoNomeDoCliente) no onClick do botão

// No ViewModel
// TODO criar a função createClient(name: String) no ViewModel
// TODO chamar em uma thread separada viewmodelscope.launch(Dispatecher.IO)
// TODO chamar o repository createClient( Client(id, name) ) calcular o id pelo tamanho da lista + 1



// DELETE CLIENT
// TODO dentro dos items da LazyColumn da lista de clientes adicionar o botão de delete
// TODO criar a função deleteClient(id: Int) como parametro do MainScreen()
// TODO chamar a função deleteClient(it.id) no onclick do botão delete

// No ViewModel
// TODO criar a função deleteClient(id: Int) no ViewModel
// TODO chamar em uma thread separada viewmodelscope.launch(Dispatecher.IO)
// TODO chamar o repository deleteClient(id)


// Documentação
// https://square.github.io/retrofit/

// curso de compose
// https://developer.android.com/courses/jetpack-compose/course?gad_source=1&gclid=CjwKCAjwooq3BhB3EiwAYqYoEhN1G38tj3TWqkmuM6vIBb8BCzhzYacy1KOFGZVB97G7HU9OFETjMBoCSEIQAvD_BwE&gclsrc=aw.ds
// https://www.youtube.com/watch?v=cDabx3SjuOY&list=PLQkwcJG4YTCSpJ2NLhDTHhi6XBNfk9WiC

// arquitetura android
// https://developer.android.com/topic/architecture


// documentação backend
// https://ktor.io/docs/server-create-a-new-project.html
