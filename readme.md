# 🎬📚 MediaTracker AI

**MediaTracker AI** es una aplicación web moderna y modular desarrollada con Java y Spring Boot, diseñada para que los usuarios puedan gestionar, seguir y descubrir contenido cultural (anime, películas, libros, mangas) con funcionalidades inteligentes de recomendación basadas en IA generativa.

---

## 🚀 Características actuales

### 🔧 Backend

- **Framework principal:** Spring Boot
- **Base de datos:** H2 persistente en archivo local
- **Arquitectura modular** por tipo de medio (anime, películas, libros...)
- **Sistema de autenticación** simple: registro y login de usuarios
- **Persistencia de contenido marcado por usuario**

### 📡 Integración con APIs externas

- **Anime:** API de [Jikan](https://jikan.moe)
- **Películas:** API de [OMDb](https://www.omdbapi.com/)
- *(próximamente libros y mangas)*

### ⭐ Funcionalidades de usuario

- Buscar animes o películas por nombre
- Guardar como "vistos" o "en seguimiento"
- Visualizar contenidos guardados por categoría y tipo
- Persistencia entre reinicios del sistema

---

## 📈 Roadmap MVP (3 semanas)

| Día | Objetivo |
|-----|----------|
| 1-2 | ✅ Backend base, integración Jikan, autenticación básica |
| 3   | ✅ Integración OMDb (películas), modularización media |
| 4   | 🔜 Integración Google Books |
| 5   | 🔜 Integración Manga (vía Jikan u otra fuente) |
| 6   | 🔜 Unificación de vistas y lógica por tipo |
| 7   | 🔜 Exportación del historial de usuario en JSON |

---

## 🤖 IA Generativa (fase avanzada)

MediaTracker AI se diseñó desde el inicio para integrar recomendaciones personalizadas mediante **modelos generativos (Gemini / GPT)**.

### 🧠 ¿Cómo funcionará?

- Se recopilan los gustos del usuario por medio de contenido guardado.
- Se formulan prompts específicos del tipo:  
    _"He visto y me ha gustado Monster, Death Note y Paranoia Agent. ¿Qué otras series me recomiendas?"_
- Se usará **un agente por medio (Gem distinto para cada tipo de contenido)** para aumentar la precisión de las respuestas.

---

## 🧱 Estructura del proyecto

```
media-tracker-ai/
├── model/
│   ├── Anime.java
│   ├── Movie.java
│   └── Book.java
├── repository/
│   ├── AnimeRepository.java
│   └── MovieRepository.java
├── service/
│   ├── AnimeService.java
│   └── MovieService.java
├── controller/
│   ├── AnimeController.java
│   └── MovieController.java
├── auth/
│   ├── User.java
│   └── AuthController.java
└── ...
```

> Cada tipo de contenido está completamente modularizado, con entidad, servicio, repositorio y controlador independientes.

---

## 🔍 ¿Por qué este enfoque?

Este proyecto nació como ejercicio de diseño limpio, modular, escalable, y orientado al futuro.  
El objetivo no es solo rastrear contenido, sino ofrecer valor al usuario gracias a la **personalización impulsada por IA generativa**.

---

## 📌 Stack técnico

- ✅ Java 17+
- ✅ Spring Boot
- ✅ JPA / Hibernate
- ✅ API REST
- ✅ H2
- ✅ Integración con APIs públicas
- ⏳ IA generativa (Gemini / GPT en endpoints personalizados)

---

## 📤 Futuro

- Exportación de recomendaciones
- UI básica con Thymeleaf o React
- Dockerización del backend
- Integración con Spring AI o LangChain4j
- Modo "modo solo lectura" como demo pública

---

## 🧑‍💻 Autor

**MediaTracker AI** ha sido diseñado y desarrollado por un ingeniero de software con background fuerte en Java y experiencia actual en IA generativa, como proyecto personal para mostrar cómo la IA puede aplicarse en sistemas culturales de manera modular, ágil y real.

---

## 📬 ¿Te interesa colaborar?

Abierto a ideas, colaboraciones, y contribuciones open source.  
Próximamente disponible en GitHub público.