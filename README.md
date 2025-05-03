# MotocoreDB - Sistema de Gestión de Taller Mecánico

## Descripción
MotocoreDB es una aplicación Java diseñada específicamente para la gestión integral de talleres mecánicos. Permite administrar eficientemente aspectos operativos del taller, con enfoque principal en la programación de citas y gestión de información.

## Características Principales
- **Agendamiento de Citas**: Programación y seguimiento de citas con clientes
- **Administración de Clientes**: Base de datos de clientes y sus vehículos
- **Control de Inventario**: Gestión de repuestos y productos utilizados
- **Reportes y Estadísticas**: Información detallada para la toma de decisiones

## Requisitos del Sistema
- **Sistema Operativo**: Windows 10/11, macOS 10.14+, o Linux (distribuciones modernas)
- **Java**: JDK 11 o superior
- **Base de Datos**: MySQL 5.7 o superior
- **Memoria RAM**: 4GB mínimo recomendado
- **Espacio en Disco**: 500MB disponibles
- **Resolución de Pantalla**: 1280x720 o superior

## Instalación

### Preparación del Entorno
1. Instale Java JDK 11 o superior en su sistema
2. Asegúrese de tener MySQL instalado y en funcionamiento
3. Verifique que Maven esté instalado (versión 3.6.0 o superior)

### Instalación de la Aplicación
```bash
git clone https://github.com/EstebanVC23/motocoredb.git
cd motocoredb
mvn clean install
```

### Configuración de la Base de Datos
1. La base de datos MySQL denominada `motocoredb` ya se encuentra incluida en el proyecto
2. El archivo SQL completo está disponible en `src/main/resources/motocoredb.sql`
3. Debe importar este archivo a MySQL Workbench para crear la estructura de la base de datos
4. Edite el archivo `config.properties` con sus propias credenciales de acceso:
```
db.url=jdbc:mysql://localhost:3306/motocoredb
db.user=su_usuario
db.password=su_contraseña
```

Nota: Las contraseñas están encriptadas en la base de datos para mayor seguridad.

### Ejecución del Sistema
```bash
mvn exec:java -Dexec.mainClass="com.motocoredb.App"
```
Alternativamente, puede ejecutar la clase App desde su entorno de desarrollo integrado (IDE).

## Credenciales de Prueba
- **Usuario Maestro/Super Usuario**: 
  - Usuario: `root`
  - Contraseña: `1234`

- **Vista de Vendedor**: 
  - Para probar la vista de vendedor, primero debe crear un usuario empleado usando la cuenta de usuario maestro.
  - Una vez creado el empleado, el sistema le proporcionará las credenciales correspondientes para acceder con este perfil.

## Primera Configuración
Al iniciar la aplicación por primera vez, se recomienda:
- Establecer los parámetros básicos del taller (nombre, dirección, datos de contacto)
- Crear las cuentas de usuario para los diferentes roles (administrador, mecánico, recepcionista)

## Módulos del Sistema

### Agendamiento de Citas
- Programar nuevas citas (exclusivamente para agendar, sin gestión de servicios)
- Visualizar agenda (diaria, semanal, mensual)
- Asignar fechas y horarios

**Nota importante**: El sistema solo permite agendar citas, no gestiona servicios asociados a las mismas.

### Administración de Clientes
- Registro de clientes con datos personales y de contacto
- Gestión de vehículos asociados a cada cliente
- Historial de citas agendadas

### Control de Inventario
- Catálogo completo de productos y repuestos
- Control de existencias
- Gestión de proveedores
- Registro de movimientos (entradas, salidas, ajustes)

**Nota**: El sistema no cuenta con funcionalidad para enviar alertas sobre niveles de inventario.

### Reportes y Estadísticas
- Análisis de citas programadas por período
- Información de clientes registrados
- Niveles de inventario actual
- Exportación de informes en formatos PDF, Excel y CSV

## Solución de Problemas

| Problema | Posible Causa | Solución |
|----------|---------------|----------|
| Error de conexión a la base de datos | Credenciales incorrectas | Verificar archivo config.properties |
| | MySQL no está en ejecución | Iniciar el servicio de MySQL |
| La aplicación se ejecuta lentamente | Recursos insuficientes | Cerrar aplicaciones no utilizadas |
| | Base de datos muy grande | Considerar una limpieza o archivado |
| Errores en los reportes | Datos inconsistentes | Ejecutar la herramienta de verificación de datos |
| | Permisos insuficientes | Verificar rol del usuario |

## Información Técnica

### Arquitectura del Sistema
MotocoreDB sigue una arquitectura multicapa (MVC):
- **Modelo**: Representa los datos y la lógica de negocio (`package com.motocoredb.models`)
- **Vista**: Interfaces gráficas desarrolladas con Swing (`package com.motocoredb.views`)
- **Controlador**: Maneja la interacción entre la vista y el modelo (`package com.motocoredb.controllers`)

Además, se implementa un patrón DAO (Data Access Object) para abstraer y encapsular el acceso a la base de datos.

### Tecnologías Utilizadas
- **Java**: Lenguaje de programación principal (JDK 11+)
- **Swing**: Biblioteca para la interfaz gráfica
- **MySQL**: Sistema de gestión de base de datos
- **Maven**: Gestión de dependencias y construcción
- **jCalendar y LGoodDatePicker**: Componentes para la gestión de fechas
- **SLF4J + Logback**: Framework de logging

### Estructura de Directorios
```
motocoredb/
├── src/
│   ├── main/
│   │   ├── java/com/motocoredb/
│   │   │   ├── controllers/  # Controladores de la aplicación
│   │   │   ├── dao/          # Objetos de acceso a datos
│   │   │   │   ├── impl/     # Implementaciones concretas
│   │   │   │   └── interfaces/ # Interfaces DAO
│   │   │   ├── models/       # Entidades y modelos de datos
│   │   │   ├── services/     # Servicios de negocio
│   │   │   └── views/        # Interfaces gráficas
│   │   │       ├── Main/     # Ventana principal
│   │   │       ├── forms/    # Formularios específicos
│   │   │       └── utils/    # Componentes reutilizables
│   │   └── resources/        # Recursos estáticos
│   │       └── motocoredb.sql # Archivo SQL de la base de datos
│   └── test/                 # Pruebas unitarias
├── lib/                      # Bibliotecas externas
└── config.properties         # Archivo de configuración
```

## Declaración de Derechos de Uso
Este proyecto se proporciona bajo la licencia MIT, lo que permite a otros estudiantes:
- Utilizar el código como base para mejoras y extensiones
- Estudiar la estructura y diseño para fines educativos
- Implementar funcionalidades adicionales
- Redistribuir versiones modificadas

Se solicita únicamente la atribución adecuada al trabajo original.

## Posibles Mejoras
El sistema actual podría beneficiarse de las siguientes mejoras:

### Mejoras de Diseño
- Implementación de una interfaz más moderna con Material Design o JavaFX
- Mejora de la experiencia de usuario con iconografía más intuitiva
- Diseño responsive para adaptarse a diferentes resoluciones de pantalla
- Temas oscuro/claro seleccionables por el usuario

### Infraestructura
- Migración de la base de datos a la nube (AWS RDS, Azure SQL o Google Cloud SQL)
- Implementación de un sistema de respaldos automáticos
- Arquitectura cliente-servidor para permitir múltiples usuarios simultáneos
- Implementación de API REST para integración con aplicaciones móviles

### Funcionalidades Adicionales
- Sistema de notificaciones por correo electrónico para alertas de inventario
- Implementación de SMS para recordatorios de citas a clientes
- Integración con pasarelas de pago para cobros electrónicos
- Módulo de facturación electrónica conforme a normativas locales
- Dashboard analítico con KPIs relevantes para la gestión del taller

### Optimización de Código
- Refactorización para mejorar la reutilización de componentes
- Optimización de consultas SQL para mejorar el rendimiento
- Implementación de caché para consultas frecuentes
- Mejora en el manejo de excepciones y logging
- Implementación de tests automatizados con JUnit