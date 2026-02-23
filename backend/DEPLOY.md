# Despliegue en Railway

## Opción 1: Despliegue automático con GitHub

1. Sube tu código a GitHub
2. Ve a [Railway.app](https://railway.app)
3. Inicia sesión con GitHub
4. Click "New Project" → "Deploy from GitHub repo"
5. Selecciona tu repositorio
6. Railway detectará automáticamente el Dockerfile

## Opción 2: Railway CLI

```bash
# Instalar CLI
npm install -g @railway/cli

# Iniciar sesión
railway login

# Iniciar proyecto
railway init

# Desplegar
railway up
```

## Variables de entorno

En el dashboard de Railway, configura:

| Variable | Valor |
|----------|-------|
| PORT | 8080 |
| OLLAMA_URL | URL de tu servicio Ollama |
| SD_URL | URL de Stable Diffusion |
| SEARCH_API_KEY | Tu API key (opcional) |
| HA_URL | URL de Home Assistant (opcional) |
| HA_TOKEN | Token de HA (opcional) |

## Notas importantes

El backend requiere **Ollama** para el chat con IA. Tienes dos opciones:

### Opción A: Ollama en el mismo Railway
Railway no permite procesos largos, así que necesitas:
1. Railway para el backend API
2. Tu PC/servidor ejecutando Ollama
3. Configurar OLLAMA_URL con la IP pública de tu PC

### Opción B: Usar API de terceros
Cambia OLLAMA_URL para usar:
- OpenAI: https://api.openai.com/v1
- Anthropic: https://api.anthropic.com
- Ollama Cloud: https://api.ollama.ai (si disponible)

## Obtener URL pública de tu PC (para Ollama local)

```bash
# Usando ngrok (https://ngrok.com)
ngrok tcp 11434
```

Luego configura la URL que te da ngrok en Railway.
