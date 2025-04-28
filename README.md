# README - Sistema de Gestión de Taller Mecánico (MotocoreDB)

## Descripción del Proyecto
Este proyecto es una aplicación Java para la gestión integral de talleres mecánicos, que permite administrar citas, clientes, productos usados, alertas y otros aspectos operativos. La aplicación sigue una arquitectura multicapa (MVC) y utiliza Swing para la interfaz gráfica.

## Estructura del Proyecto

```
motocoredb/
├── pom.xml
├── README.md
├── alertconfig.properties
├── config.properties
├── .vscode/
│   └── settings.json
├── lib/
│   ├── jcalendar-1.4.jar
│   └── LGoodDatePicker-11.2.1.jar
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── motocoredb/
│   │   │           ├── controllers/
│   │   │           ├── dao/
│   │   │           │   ├── impl/
│   │   │           │   └── interfaces/
│   │   │           ├── models/
│   │   │           ├── services/
│   │   │           └── views/
│   │   │               ├── Main/
│   │   │               │   ├── navbar/
│   │   │               │   └── panels/
│   │   │               ├── forms/
│   │   │               │   └── appointments/
│   │   │               └── utils/
│   │   └── resources/
│   │       ├── config.properties
│   │       └── img/
│   └── test/
│       └── java/
└── target/
    ├── classes/
    └── generated-sources/
```

## Requisitos del Sistema

- Java JDK 11 o superior
- Maven 3.6.0 o superior
- MySQL 5.7 o superior (o compatible)
- 4GB RAM mínimo
- 500MB de espacio en disco

## Configuración Inicial

1. **Base de Datos**:
   - Crear una base de datos MySQL llamada `motocoredb`
   - Ejecutar el script SQL ubicado en `src/main/resources/sql/init_db.sql`

2. **Configuración**:
   - Editar el archivo `config.properties` con las credenciales de tu base de datos:
     ```
     db.url=jdbc:mysql://localhost:3306/motocoredb
     db.user=tu_usuario
     db.password=tu_contraseña
     ```

## Instalación

```bash
git clone https://github.com/EstebanVC23/motocoredb.git
cd motocoredb
mvn clean install
```

## Ejecución

```bash
mvn exec:java -Dexec.mainClass="com.motocoredb.App"
```

O ejecutar la clase `App` desde tu IDE favorito.

## Características Principales

- Gestión de citas (creación, modificación, cancelación)
- Administración de clientes
- Control de inventario de productos usados
- Sistema de alertas para citas próximas
- Interfaces diferenciadas para administradores y personal
- Reportes y estadísticas

## Dependencias Principales

- MySQL Connector
- SLF4J + Logback (logging)
- Apache Commons Lang3
- JUnit (para pruebas)
- jCalendar (para selección de fechas)
- LGoodDatePicker (para manejo de fechas/horas)

## Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo LICENSE para más detalles.

## Contribución

Las contribuciones son bienvenidas. Por favor, sigue estos pasos:
1. Haz un fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Haz commit de tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Haz push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

**Nota**: Este README es un template básico. Ajustar según las necesidades específicas del proyecto.