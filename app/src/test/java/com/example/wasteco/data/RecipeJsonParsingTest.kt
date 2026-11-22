package com.example.wasteco.data

import com.example.wasteco.data.mapper.toDomain
import com.example.wasteco.data.remote.dto.RecipeListResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RecipeJsonParsingTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun testParseBackendRecipeResponse() {
        val backendJson = """
            {"payload":{"total":2,"recipes":[{"recipe_name":"Nasi Goreng Wortel Telur Spesial","match_percentage":95,"cooking_time_minutes":15,"difficulty":"Mudah","portions":2,"available_ingredients":[{"name":"nasi putih","quantity":"250 gram"},{"name":"wortel","quantity":"3 buah"},{"name":"telur","quantity":"2 butir"}],"additional_ingredients":[{"name":"minyak goreng","quantity":"2 sendok makan"},{"name":"bawang putih","quantity":"2 siung"},{"name":"bawang merah","quantity":"3 siung"},{"name":"kecap manis","quantity":"1 sendok makan"},{"name":"garam","quantity":"1 sendok teh"},{"name":"merica bubuk","quantity":"1/2 sendok teh"}],"steps":["Cincang halus bawang putih dan bawang merah, lalu potong wortel bentuk dadu kecil.","Panaskan minyak goreng dalam wajan, buat telur orak-arik lalu sisihkan di pinggir wajan.","Tumis bawang putih dan bawang merah hingga harum, masukkan potongan wortel dan masak hingga agak layu.","Masukkan nasi putih ke dalam wajan, aduk rata bersama bumbu, telur, dan wortel.","Tambahkan kecap manis, garam, dan merica, lalu aduk terus dengan api besar hingga nasi panas dan matang merata."],"smart_tips":"Gunakan nasi putih yang sudah dingin atau dimasukkan kulkas semalaman agar butiran nasi lebih perai dan tidak menggumpal saat digoreng."},{"recipe_name":"Tim Telur Wortel Gurih","match_percentage":85,"cooking_time_minutes":20,"difficulty":"Sangat Mudah","portions":2,"available_ingredients":[{"name":"telur","quantity":"2 butir"},{"name":"wortel","quantity":"2 buah"},{"name":"nasi putih","quantity":"1 mangkuk kecil sebagai pelengkap"}],"additional_ingredients":[{"name":"air","quantity":"150 ml"},{"name":"garam","quantity":"1/2 sendok teh"},{"name":"kaldu bubuk","quantity":"1/2 sendok teh"},{"name":"daun bawang","quantity":"1 batang"}],"steps":["Kupas wortel, lalu parut atau cincang sangat halus agar cepat matang saat dikukus.","Kocok lepas telur dalam mangkuk tahan panas, tambahkan air, garam, dan kaldu bubuk.","Masukkan parutan wortel dan irisan daun bawang ke dalam adonan telur, aduk rata.","Kukus mangkuk berisi adonan telur dalam kukusan yang sudah dipanaskan selama sekitar 15 menit.","Angkat tim telur selagi hangat dan sajikan bersama nasi putih."],"smart_tips":"Tutup wadah pengukus dengan kain bersih agar tetesan air uap tidak jatuh langsung ke permukaan tim telur sehingga teksturnya tetap lembut."}]}}
        """.trimIndent()

        val response = json.decodeFromString<RecipeListResponse>(backendJson)
        assertNotNull(response.payload)
        assertEquals(2, response.payload.recipes.size)

        val recipe1 = response.payload.recipes[0].toDomain()
        assertEquals("Nasi Goreng Wortel Telur Spesial", recipe1.name)
        assertEquals(95, recipe1.matchPercentage)
        assertEquals(15, recipe1.cookingTimeMinutes)
        assertEquals(2, recipe1.servings)
        assertEquals(3, recipe1.usedIngredients.size)
        assertEquals("nasi putih", recipe1.usedIngredients[0].name)
        assertEquals(250.0, recipe1.usedIngredients[0].quantity, 0.01)
        assertEquals("gram", recipe1.usedIngredients[0].unit)
        assertEquals(6, recipe1.additionalIngredients.size)
        assertEquals("minyak goreng: 2 sendok makan", recipe1.additionalIngredients[0])
        assertEquals(5, recipe1.instructions.size)
        assertNotNull(recipe1.zeroWasteTips)
    }
}
