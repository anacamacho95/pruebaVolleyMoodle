package com.example.pruebavolley

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    // URL de la API para obtener posts
    //val url = "https://jsonplaceholder.typicode.com/posts"

    val url ="https://firestore.googleapis.com/v1/projects/alimentosbd-7be67/databases/(default)/documents/alimentos"

    // Crear una cola de solicitudes
    lateinit var cola:RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cola = Volley.newRequestQueue(this)

        //probarGet() //de un web service público

        // Probar acceso a FB como api rest
        pruebaPostAlimento("nuevo alimento",120.0)
        pruebaGetAlimentos()
        pruebaGetUnAlimento()
        pruebaDeleteAlimento()
        pruebaActualizaUnAlimento()

    }

    private fun pruebaGetUnAlimento() {

        val url = "https://firestore.googleapis.com/v1/projects/alimentosbd-7be67/databases/(default)/documents/alimentos/Ah84prKpWyNsQfLZ0qas"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.d("Firestore", "Documento obtenido: $response")
            },
            Response.ErrorListener { error ->
                Log.e("Firestore", "Error: $error")
            })

        cola.add(jsonObjectRequest)

    }

    private fun pruebaActualizaUnAlimento() {
        val url = "https://firestore.googleapis.com/v1/projects/alimentosbd-7be67/databases/(default)/documents/alimentos/UHXUPAERGxXQDDi71z0T"

        val alimento = JSONObject()
        val fields = JSONObject()
        fields.put("nombre", JSONObject().put("stringValue", "aceite actualizado"))
        fields.put("kcal", JSONObject().put("doubleValue", 200.0))
        alimento.put("fields", fields)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.PATCH, url, alimento,
            Response.Listener { response ->
                Log.d("Firestore", "Documento actualizado: $response")
            },
            Response.ErrorListener { error ->
                Log.e("Firestore", "Error: $error")
            })

       cola.add(jsonObjectRequest)

    }

    private fun pruebaDeleteAlimento() {
        val url = "https://firestore.googleapis.com/v1/projects/alimentosbd-7be67/databases/(default)/documents/alimentos/dM4JMeofJ4rg3UT03oFO"

        val solicitud = StringRequest(Request.Method.DELETE, url,
            Response.Listener { response ->
                Log.d("Firestore", "Documento eliminado")
            },
            Response.ErrorListener { error ->
                error.networkResponse?.let {
                    val errorData = String(it.data)
                    Log.e("Firestore", "Error: $errorData")
                }
            })

        cola.add(solicitud)


    }

    private fun pruebaPostAlimento(nombre:String, kcal:Double) {

        val alimento = JSONObject()
        val fields = JSONObject()
        fields.put("nombre", JSONObject().put("stringValue", nombre))
        fields.put("kcal", JSONObject().put("doubleValue", kcal))
        alimento.put("fields", fields)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, alimento,
            Response.Listener { response ->
                Log.d("Firestore", "Documento creado: $response")
            },
            Response.ErrorListener { error ->
                error.networkResponse?.let {
                    val errorData = String(it.data)
                    Log.e("Firestore", "Error: $errorData")
                }
            })

        cola.add(jsonObjectRequest)

    }

    private fun pruebaGetAlimentos() {
        val solicitud = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                // Aquí asumimos que la respuesta contiene un objeto con una propiedad 'documents'
                val documents = response.getJSONArray("documents")
                for (i in 0 until documents.length()) {
                    val item = documents.getJSONObject(i)
                    val fields = item.getJSONObject("fields")
                    val nombre = fields.getJSONObject("nombre").getString("stringValue")
                    Log.d("Firestore", "Nombre: $nombre")

                }
            },
            Response.ErrorListener { error ->
                Log.e("Firestore", "Error: $error")
            })
        cola.add(solicitud)
    }

    private fun probarGet() {
        // Crear la solicitud GET
        val solicitud = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { respuesta ->
                // Procesar la respuesta. Ejemplo: Imprimir los primeros 10 títulos de posts
                for (i in 0 until 10) {
                    val post = respuesta.getJSONObject(i)
                    Log.d("Post", "Post #${post.getInt("id")}: ${post.getString("title")}")
                }
            },
            Response.ErrorListener { error ->
                // Manejar el error
                Log.d("Error", "Error en la solicitud: $error")
            })

        // Añadir la solicitud a la cola
        cola.add(solicitud)
    }
}
