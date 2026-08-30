package tm.true.loss.core.network.di
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun provideJson(): Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    @Provides @Singleton fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder().build()
    @Provides @Singleton fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("https://api.true.loss/").client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
}
