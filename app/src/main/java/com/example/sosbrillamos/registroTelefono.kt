package com.example.sosbrillamos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class registroTelefono : AppCompatActivity() {

    private lateinit var etTelefono: EditText
    private lateinit var etTelefono2: EditText
    private lateinit var etTelefono3: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_telefono)

        etTelefono = findViewById(R.id.etTelefono)
        etTelefono2 = findViewById(R.id.etTelefono2)
        etTelefono3 = findViewById(R.id.etTelefono3)

        val enviarButton: Button = findViewById(R.id.buttonRegist)
        enviarButton.setOnClickListener {
            val valor1 = etTelefono.text.toString()
            val valor2 = etTelefono2.text.toString()
            val valor3 = etTelefono3.text.toString()

            val intent = Intent(this, sosMessage::class.java)
            intent.putExtra("valor1", valor1)
            intent.putExtra("valor2", valor2)
            intent.putExtra("valor3", valor3)
            startActivity(intent)

        }
    }
}