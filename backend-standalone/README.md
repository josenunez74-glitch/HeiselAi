# HeiselAI Backend

Backend para la aplicación de IA de HeiselAI. Proporciona APIs para chat, generación de imágenes, búsqueda web, control IoT y más.

## Características

- 🤖 Chat con IA (usando Ollama - modelos open source)
- 🖼️ Generación de imágenes (Stable Diffusion)
- 🔍 Búsqueda web
- 🏠 Control de dispositivos IoT (luces, puertas, alarmas, etc.)
- 📱 Control del teléfono
- 💬 Especializado en: Medicina, Leyes dominicanas, IT/Programación

## Requisitos

- Kotlin 1.9+
- Java 17+
- Ollama (para chat)
- Stable Diffusion API (para imágenes)
- Home Assistant (opcional, para IoT)

## Instalación

1. Instalar dependencias:
```bash
cd backend
./gradlew build
```

2. Configurar variables de entorno:
```bash
export PORT=8080
export OLLAMA_URL=http://localhost:11434
export SD_URL=http://localhost:7860
export SEARCH_API_KEY=tu_api_key
export HA_URL=http://homeassistant.local:8123
export HA_TOKEN=tu_token
export DEFAULT_MODEL=llama3
```

3. Ejecutar:
```bash
./gradlew run
```

## Endpoints

### Chat
```
POST /api/chat
{
  "messages": [{"role": "user", "content": "Hola"}],
  "model": "llama3"
}

GET /api/models
```

### Imágenes
```
POST /api/image/generate
{
  "prompt": "una casa en el campo",
  "width": 512,
  "height": 512,
  "steps": 20
}
```

### Búsqueda
```
POST /api/search
{
  "query": "qué es Kotlin",
  "maxResults": 5
}

GET /api/search/web?q=tu+búsqueda
```

### IoT
```
POST /api/iot/control
{
  "device": "luz sala",
  "action": "encender"
}

GET /api/iot/devices
```

### Teléfono
```
POST /api/phone/command
{
  "action": "call",
  "params": {"number": "+18095551234"}
}

POST /api/phone/command
{
  "action": "sms",
  "params": {"number": "+18095551234", "message": "Hola"}
}

POST /api/phone/command
{
  "action": "open_app",
  "params": {"package": "com.whatsapp"}
}
```

### Salud
```
GET /api/health
```

## Modelos recomendados

Para chat (Ollama):
- `llama3` - Muy inteligente, recomendado
- `mistral` - Rápido y eficiente
- `qwen` - Bueno para tareas específicas

Para imágenes:
- Stable Diffusion WebUI (local)
- O usar APIs como DALL-E, Midjourney

## Arquitectura

```
com.heiselai/
├── Application.kt      # Punto de entrada
├── config/             # Configuración
├── models/             # Modelos de datos
├── routes/             # Endpoints API
├── services/           # Lógica de negocio
└── utils/              # Utilidades
```

## Despliegue en la nube

Ver [DEPLOY.md](./DEPLOY.md) para instrucciones de despliegue en Railway.

### Resumen rápido:

1. Sube el código a GitHub
2. Ve a [Railway.app](https://railway.app)
3. Crea un nuevo proyecto desde GitHub
4. Railway desplegará automáticamente con Docker
5. Configura las variables de entorno en el dashboard

### Nota sobre IA

El backend requiere Ollama para el chat. Tienes dos opciones:
- **Local**: Ejecuta Ollama en tu PC y usa ngrok para exponerlo
- **Cloud**: Usa APIs de OpenAI/Claude configurando las URLs
