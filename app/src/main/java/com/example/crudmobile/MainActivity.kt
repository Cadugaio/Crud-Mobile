package com.example.crudmobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

    init {
        // Dispatchers.IO faz a comunicação com o servidor em uma thread separada para evitar
        // travamentos na tela (busca os dados em segundo plano)
        viewModelScope.launch(Dispatchers.IO) {
            // Busca os produtos do repositório
            crudRepository.getProducts().let {
                // Atualiza a variavel de produtos com os dados retornados do servidor
                products.postValue(it)
            }
        }

        // TODO client
//        viewModelScope.launch(Dispatchers.IO) {
//            // Busca os produtos do repositório
//            crudRepository.getProducts().let {
//                // Atualiza a variavel de produtos com os dados retornados do servidor
//                products.postValue(it)
//            }
//        }
    }

    fun updateProductsList() {
        // Dispatchers.IO faz a comunicação com o servidor em uma thread separada para evitar
        // travamentos na tela (busca os dados em segundo plano)
        viewModelScope.launch(Dispatchers.IO) {
            // Busca os produtos do repositório
            crudRepository.getProducts().let {
                // Atualiza a variavel de produtos com os dados retornados do servidor
                products.postValue(it)
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

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        // Envia a lista de produtos para ser desenhado na UI
                        productsList.value ?: emptyList(),
                        {
                            mainVM.updateProductsList()
                        }

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
    // clients: List<Client>,
    // updateClientsList: () -> Unit,
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = it.name)
                    Text(text = it.price)
                }
            }

        }
        Button(onClick = { updateProductsList() }) {
            Text(text = "Update")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CrudmobileTheme {
        MainScreen(
            products = listOf(
                Product(1, "Teste", "R$ 123"),
                Product(2, "Teste1", "R$ 123"),
                Product(3, "Teste2", "R$ 123"),
                Product(4, "Teste3", "R$ 123"),
            ),
            updateProductsList = {}
        )
    }
}


// TODO mapear o objeto cliente (crud),
// TODO criar a interface client API e mapear a API
// TODO no crud repository criar outra variavel para interface client api
// TODO no crud repository criar as funçoes endpoint

// TODO no view model criar variavel para a lista de clientes
// TODO no view model implementar a chamada do repositório para o getClients

// TODO na main activity criar variavel clients e observar as alterações da variavel do view model (observeAsState)
// TODO passar a lista de clientes para a UI MainScreen
// TODO passar uma função lambda para a UI MainScreen para atualizar a lista de clientes
// TODO implementar outra LazyColumn para mostrar a lista de clientes
// TODO implmentar outro botão para atualizar a lista de clientes
