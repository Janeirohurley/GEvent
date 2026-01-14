package com.janeirohurley.gevent.data.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Client Retrofit singleton pour les appels API
 */
object RetrofitClient {

    // TODO: Remplacer par votre URL d'API réelle
    private const val BASE_URL = "http://192.168.0.125:8000/api/"

    // Token d'authentification (stocké après login)
    private var authToken: String? = null

    /**
     * Définir le token d'authentification
     */
    fun setAuthToken(token: String?) {
        authToken = token
    }

    /**
     * Intercepteur pour ajouter le token d'authentification
     */
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        // Liste des endpoints qui ne nécessitent PAS d'authentification
        val noAuthEndpoints = listOf("auth/login/", "auth/register/")
        val isNoAuthEndpoint = noAuthEndpoints.any { request.url.encodedPath.contains(it) }

        // Ajouter le token UNIQUEMENT si disponible ET si ce n'est pas un endpoint public
        if (!isNoAuthEndpoint) {
            authToken?.let { token ->
                val authHeader = "Token $token"
                requestBuilder.addHeader("Authorization", authHeader)
                println("🔑 AuthInterceptor: Adding token to ${request.url.encodedPath}")
                println("🔑 Authorization header: $authHeader")
            } ?: run {
                println("⚠️ AuthInterceptor: No token available for ${request.url.encodedPath}")
            }
        } else {
            println("ℹ️ AuthInterceptor: Skipping auth for public endpoint ${request.url.encodedPath}")
        }

        // Ajouter les headers communs
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")

        chain.proceed(requestBuilder.build())
    }

    /**
     * Intercepteur de logging pour le debug
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Client OkHttp avec intercepteurs
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    /**
     * Configuration Gson avec désérialiseur personnalisé pour Event
     * Gère la conversion du champ category (objet -> string)
     */
    private val gson = GsonBuilder()
        .registerTypeAdapter(com.janeirohurley.gevent.data.model.Event::class.java, EventDeserializer())
        .create()

    /**
     * Instance Retrofit
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Instance de l'API service
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
