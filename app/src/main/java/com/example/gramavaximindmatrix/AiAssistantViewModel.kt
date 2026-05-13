package com.example.gramavaximindmatrix

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AiAssistantViewModel : ViewModel() {
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    init {
        _messages.add(ChatMessage("Hello! I am your GramVaxi AI Assistant. Tell me about your animal's symptoms, and I can suggest possible care tips.", false))
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        _messages.add(ChatMessage(text, true))
        
        viewModelScope.launch {
            delay(1000) // Simulate AI thinking
            val response = getRuleBasedResponse(text.lowercase())
            _messages.add(ChatMessage(response, false))
        }
    }

    private fun getRuleBasedResponse(input: String): String {
        return when {
            input.contains("fever") && (input.contains("mouth") || input.contains("foot") || input.contains("sores")) -> {
                "Based on the symptoms (Fever, Mouth/Foot sores), it could be Foot and Mouth Disease (FMD).\n\nTips:\n1. Isolate the animal immediately.\n2. Apply antiseptic on sores.\n3. Consult a vet for vaccination and treatment."
            }
            input.contains("lump") || input.contains("skin") || input.contains("bumps") -> {
                "Lumps on the skin might indicate Lumpy Skin Disease (LSD).\n\nTips:\n1. Use insect repellents to prevent spread by flies/mosquitoes.\n2. Keep the animal hydrated.\n3. Report to nearest veterinary clinic."
            }
            input.contains("cough") || input.contains("breathing") || input.contains("nose") -> {
                "Respiratory issues could indicate Pneumonia or Haemorrhagic Septicaemia (HS).\n\nTips:\n1. Keep the animal in a well-ventilated, dry area.\n2. Avoid exposure to cold/rain.\n3. Seek veterinary help for antibiotics."
            }
            input.contains("diarrhea") || input.contains("loose motion") -> {
                "Diarrhea can be caused by worms or infections.\n\nTips:\n1. Provide plenty of clean water and electrolytes.\n2. Check deworming history.\n3. Monitor for blood in stool."
            }
            input.contains("not eating") || input.contains("low appetite") -> {
                "Loss of appetite is a common sign of illness.\n\nTips:\n1. Check temperature (fever).\n2. Try offering fresh green fodder.\n3. If it persists for more than 24 hours, call a vet."
            }
            else -> {
                "I'm not quite sure about those symptoms. It's always best to consult a professional veterinarian for an accurate diagnosis."
            }
        }
    }
}
