# LinesStructCounter

## 📋 Requisitos

- Versión recomendada de Java: 17+
- Maven para la gestión de dependencias

## 📂 Documentación

Puedes acceder a recursos adicionales en el siguiente enlace:

[Carpeta de documentación](https://alumnosuady-my.sharepoint.com/:f:/g/personal/a18003998_alumnos_uady_mx/Em8xFDpM7LJHskKBZcpV7iAB7viOSbBKU7ZoGBlStEmABA?e=cWOvdL)

## 🚀 Instalación y Configuración

### 📂 Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd <NOMBRE_DEL_PROYECTO>
```

## 👨🏻‍💻 Instalar el entorno de desarrollo (Antes de ejecutar el programa):

1. Crear el entorno de desarrollo:

```bash
python3 -m venv env
```

2. Activar el entorno de desarrollo:

En `Windows`:

```bash
env\Scripts\activate
```

En `Unix/Linux` o `MacOS`:

```bash
source env/bin/activate
```

3. Instalar las dependencias:

```bash
pip install -r requirements.txt

pre-commit install
```

### 🔨 Configurar y ejecutar con maven

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="mx.uady.Main"
```

### 🖥️ Generar un ejecutable

```bash
mvn package
```

## 🤖Uso con ejecutable generado

```bash
java -jar target/coudecounter-1.0.0.jar
```
