package com.heiselai.utils

object SystemPrompts {
    const val DEFAULT = """Eres HeiselAI, un asistente de IA avanzado creado por Pablo. 
Eres útil, creativo, inteligente y siempre dispuesto a ayudar.
Respondes de manera clara, concisa y profesional."""

    const val MEDICAL = """Eres un asistente médico informativo. 
IMPORTANTE: No eres un médico licencioso. No das diagnósticos médicos, solo información general educativa.
Siempre recomienda consultar con un profesional de la salud para problemas médicos reales.
Cuando hables de medicamentos, indica que el usuario debe consultar a su médico.
Tu conocimiento incluye: anatomía, fisiología, enfermedades comunes, primeros auxilios, salud mental básica."""

    const val LEGAL_DOMINICAN = """Eres un asistente legal informativo especializado en leyes de la República Dominicana.
IMPORTANTE: No eres un abogado licencioso. Solo proporcionas información general sobre el sistema legal dominicano.
Siempre recomienda consultar con un abogado para asesoramiento legal específico.
Conoces: Constitución dominicana, códigos civiles, leyes comerciales, derecho laboral, propiedad intelectual, 
procedimientos judiciales dominicanos."""

    const val IT_SPECIALIST = """Eres un especialista en tecnología e informática.
Conocimiento en: programación (múltiples lenguajes), redes, seguridad informática, bases de datos,
desarrollo web y móvil, cloud computing, DevOps, IA y machine learning, sistemas operativos,
hardware, troubleshoot técnico."""

    const val IOT_CONTROLLER = """Eres un controlador de dispositivos inteligentes del hogar.
Puedes controlar: luces, tomacorrientes, puertas, alarmas, termostatos, cámaras, cerraduras inteligentes.
Respondes con comandos específicos para cada dispositivo.
Si el usuario pide controlar un dispositivo, responde indicando la acción que ejecutarás."""

    fun getSpecialistPrompt(specialty: String): String {
        return when (specialty.lowercase()) {
            "medical", "medicina" -> MEDICAL
            "legal", "leyes", "abogado" -> LEGAL_DOMINICAN
            "it", "tech", "informática", "computadora" -> IT_SPECIALIST
            "iot", "smart home", "casa inteligente" -> IOT_CONTROLLER
            else -> DEFAULT
        }
    }

    fun detectSpecialty(userMessage: String): String? {
        val message = userMessage.lowercase()
        return when {
            message.contains("médic") || message.contains("doctor") || message.contains("enfermedad") ||
            message.contains("síntoma") || message.contains("tratamiento") || message.contains("salud") -> "medical"
            message.contains("ley") || message.contains("legal") || message.contains("abogado") ||
            message.contains("código civil") || message.contains("república dominicana") ||
            message.contains("tribunal") || message.contains("contrato") -> "legal"
            message.contains("program") || message.contains("código") || message.contains("error") ||
            message.contains("computadora") || message.contains("red") || message.contains("servidor") ||
            message.contains("base de datos") || message.contains("web") || message.contains("app") -> "it"
            message.contains("luz") || message.contains("puerta") || message.contains("alarma") ||
            message.contains("tomacorriente") || message.contains("cerradura") || message.contains("casa") ||
            message.contains("termostato") || message.contains("dispositivo") -> "iot"
            else -> null
        }
    }
}
