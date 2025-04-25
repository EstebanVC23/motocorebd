MotoCoreDB/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── motocoredb/
│   │   │   │   │   ├── config/                 # Configuración de la aplicación
│   │   │   │   │   │   └── DatabaseConfig.java
│   │   │   │   │   ├── controllers/            # Controladores (lógica de presentación)
│   │   │   │   │   │   ├── AuthController.java
│   │   │   │   │   │   ├── ClienteController.java
│   │   │   │   │   │   └── ... (otros controladores)
│   │   │   │   │   ├── dao/                   # Data Access Objects (capa de acceso a datos)
│   │   │   │   │   │   ├── interfaces/         # Interfaces DAO
│   │   │   │   │   │   │   ├── IUsuarioDAO.java
│   │   │   │   │   │   │   └── ... (otras interfaces)
│   │   │   │   │   │   ├── impl/               # Implementaciones DAO
│   │   │   │   │   │   │   ├── UsuarioDAOImpl.java
│   │   │   │   │   │   │   └── ... (otras implementaciones)
│   │   │   │   │   ├── models/                 # Modelos de datos (entidades)
│   │   │   │   │   │   ├── Usuario.java
│   │   │   │   │   │   ├── Cliente.java
│   │   │   │   │   │   └── ... (otras entidades)
│   │   │   │   │   ├── services/               # Lógica de negocio
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── ClienteService.java
│   │   │   │   │   │   └── ... (otros servicios)
│   │   │   │   │   ├── utils/                  # Utilidades
│   │   │   │   │   │   ├── DBConnection.java
│   │   │   │   │   │   ├── PasswordUtils.java
│   │   │   │   │   │   └── ... (otras utilidades)
│   │   │   │   │   └── App.java                # Clase principal
│   │   ├── resources/
│   │   │   ├── sql/                            # Scripts SQL
│   │   │   │   └── create_tables.sql
│   │   │   └── config.properties               # Configuración de la base de datos
├── lib/                                        # Dependencias externas
└── pom.xml                                     # Configuración de Maven