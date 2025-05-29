-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 29-05-2025 a las 14:17:59
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `concesionario`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `citasdeprueba`
--

CREATE TABLE `citasdeprueba` (
  `id` bigint(20) NOT NULL,
  `fechahora` datetime(6) DEFAULT NULL,
  `estado` enum('ACEPTADA','CANCELADA','COMPLETADA','PENDIENTE','RECHAZADA') DEFAULT NULL,
  `idcliente` bigint(20) DEFAULT NULL,
  `idvehiculo` bigint(20) DEFAULT NULL,
  `idcitadeprueba` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `citasdeprueba`
--

INSERT INTO `citasdeprueba` (`id`, `fechahora`, `estado`, `idcliente`, `idvehiculo`, `idcitadeprueba`) VALUES
(1, '2025-04-16 16:26:40.000000', 'COMPLETADA', 1, 14, NULL),
(2, '2025-05-30 12:39:00.000000', 'RECHAZADA', 1, 6, NULL),
(3, '2025-05-30 20:34:00.000000', 'ACEPTADA', 1, 3, NULL),
(4, '2025-05-31 14:07:00.000000', 'ACEPTADA', 9, 1, NULL),
(5, '2025-06-05 19:56:00.000000', 'CANCELADA', 9, 3, NULL),
(6, '2025-05-31 15:05:00.000000', 'CANCELADA', 1, 39, NULL),
(7, '2025-06-17 17:01:00.000000', 'ACEPTADA', 1, 7, NULL),
(8, '2025-06-04 17:13:00.000000', 'ACEPTADA', 9, 15, NULL),
(9, '2025-05-31 18:14:00.000000', 'RECHAZADA', 9, 6, NULL),
(10, '2025-06-05 16:15:00.000000', 'PENDIENTE', 10, 40, NULL),
(11, '2025-05-31 17:16:00.000000', 'PENDIENTE', 10, 88, NULL),
(12, '2025-06-01 18:16:00.000000', 'PENDIENTE', 10, 33, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `id` bigint(20) NOT NULL,
  `dni` varchar(255) DEFAULT NULL,
  `dechareg` date DEFAULT NULL,
  `idusuario` bigint(20) NOT NULL,
  `vehiculo_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`id`, `dni`, `dechareg`, `idusuario`, `vehiculo_id`) VALUES
(1, '58446725e', '2025-04-07', 3, NULL),
(3, '34252519e', '2025-04-09', 6, NULL),
(5, '10897575z', '2025-04-09', 8, NULL),
(6, '12345678z', '2025-05-28', 73, NULL),
(9, '58446725e', '2025-05-28', 76, NULL),
(10, '60781344a', '2025-05-29', 77, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `evaluacion`
--

CREATE TABLE `evaluacion` (
  `id` bigint(20) NOT NULL,
  `comentario` varchar(255) DEFAULT NULL,
  `fechahora` datetime(6) DEFAULT NULL,
  `calificación` int(11) DEFAULT NULL,
  `idcliente` bigint(20) DEFAULT NULL,
  `idvehiculo` bigint(20) DEFAULT NULL,
  `idevaluacion` bigint(20) DEFAULT NULL,
  `idcitadeprueba` bigint(20) DEFAULT NULL,
  `idcita` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `evaluacion`
--

INSERT INTO `evaluacion` (`id`, `comentario`, `fechahora`, `calificación`, `idcliente`, `idvehiculo`, `idevaluacion`, `idcitadeprueba`, `idcita`) VALUES
(3, 'Muy bien', NULL, 5, 1, 14, NULL, NULL, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `fechanac` date DEFAULT NULL,
  `apellidos` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  `codigo_recuperacion` varchar(255) DEFAULT NULL,
  `contraseña` varchar(255) DEFAULT NULL,
  `fecha_codigo_recuperacion` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `fechanac`, `apellidos`, `correo`, `nombre`, `user`, `codigo_recuperacion`, `contraseña`, `fecha_codigo_recuperacion`) VALUES
(3, '2003-11-07', 'MunizGonzalez', 'samuelmg92@educastur.es', 'samuel', 'samuglezdfdf', NULL, 'Cepi80_M', NULL),
(4, '2002-07-23', 'admin', 'admininstrador@gmail.com', 'admin', 'admin', NULL, 'admin', NULL),
(6, '1998-10-09', 'Perez', 'nachoperez@gmail.com', 'nacho', 'nachoperez', NULL, 'Cepi80_M', NULL),
(8, '1999-06-09', 'Rodriguez', 'hectorrod@gmail.com', 'hector', 'hectorrod', NULL, 'Hector123.', NULL),
(71, '2001-03-26', 'muniz', 'samuelmgj92@educastur.es', 'samuel', 'personal1', NULL, 'ffrrrffrf4444', NULL),
(73, '2002-11-15', 'Perez', 'hugoperez123@educastur.es', 'hugo', 'hugoperezz', NULL, 'Hugo_1234', NULL),
(76, '2002-03-28', 'Muñiz González', 'samuelmg392@educastur.es', 'samuel', 'danicliente', NULL, 'Danicliente_1', NULL),
(77, '2004-06-08', 'fernandez', 'miguel92@educastur.es', 'miguell', 'miguelcliente', NULL, 'Miguel_1234', NULL),
(78, '2007-01-10', 'Menendez', 'iker1234@educastur.es', 'Iker', 'iker1234', '151180', 'Iker_1234', '2025-05-29 13:36:06.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehiculos`
--

CREATE TABLE `vehiculos` (
  `id` bigint(20) NOT NULL,
  `añofabricacion` int(11) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `disponible` bit(1) DEFAULT NULL,
  `kilometraje` int(11) DEFAULT NULL,
  `marca` varchar(255) DEFAULT NULL,
  `modelo` varchar(255) DEFAULT NULL,
  `precio` double DEFAULT NULL,
  `tipo` enum('CAMION','COCHE','CUAD','MOTO') DEFAULT NULL,
  `foto` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `vehiculos`
--

INSERT INTO `vehiculos` (`id`, `añofabricacion`, `color`, `descripcion`, `disponible`, `kilometraje`, `marca`, `modelo`, `precio`, `tipo`, `foto`) VALUES
(1, 2021, 'azul', 'Este coupé deportivo combina elegancia con una potencia brutal de 510 CV. Con tracción total xDrive, interior en cuero Merino y una presencia que impone en cualquier calle, es pura adrenalina alemana con estilo.', b'0', 15000, 'Bmw', 'M4 competition', 70000, 'COCHE', '/images/coches/1.jpg'),
(3, 2023, 'gris', 'Un SUV con alma de deportivo. Lujoso por dentro, salvaje por fuera. Su motor V6 biturbo y el sistema EQ Boost entregan 435 CV con una suavidad impresionante.', b'0', 10000, 'Mercedes-Benz', 'Gle coupe 53 amg 4matic', 112000, 'COCHE', '/images/coches/3.jpg'),
(5, 2022, 'blanco', 'La berlina eléctrica más rápida del mundo. 1.020 CV, 0 a 100 en 2,1 segundos, y todo sin una gota de gasolina. Tecnología de vanguardia y una pantalla central que parece sacada de una nave espacial.', b'0', 8000, 'Tesla', 'Model S Plaid', 139990, 'COCHE', '/images/coches/5.jpg'),
(6, 2022, 'verde', 'Una joya italiana con corazón Ferrari. Motor V6 biturbo de 510 CV, sonido embriagador y un diseño que enamora a primera vista. Deportividad con alma y carácter.', b'0', 13025, 'Alfa romeo', 'Giulia', 92000, 'COCHE', '/images/coches/6.jpg'),
(7, 2022, 'azul', 'FH16 de alta calidad y gran rendimiento. Ideal para quienes buscan camion con carácter y fiabilidad', b'1', 6465, 'Volvo', 'Fh16', 12025, 'CAMION', '/images/coches/7.jpg'),
(9, 2023, 'gris', 'Actros de alta calidad y gran rendimiento. Ideal para quienes buscan camion con carácter y fiabilidad.', b'1', 15949, 'Mercedes-Benz', 'Actros', 86059, 'CAMION', '/images/coches/9.png'),
(11, 2023, 'rojo', 'XF de alta calidad y gran rendimiento. Ideal para quienes buscan camion con carácter y fiabilidad.', b'1', 28145, 'DAF', 'XF', 142152, 'CAMION', '/images/coches/11.jpg'),
(13, 2022, 'gris', 'MT-09 de alta calidad y gran rendimiento. Ideal para quienes buscan moto con carácter y fiabilidad.', b'1', 29883, 'Yamaha', 'MT-09', 105285, 'MOTO', '/images/coches/13.jpg'),
(14, 2024, 'blanco', 'Z900 de alta calidad y gran rendimiento. Ideal para quienes buscan moto con carácter y fiabilidad.', b'1', 20544, 'Kawasaki', 'Z900', 84213, 'MOTO', '/images/coches/14.jpg'),
(15, 2024, 'rojo', 'CBR600RR de alta calidad y gran rendimiento. Ideal para quienes buscan moto con carácter y fiabilidad.', b'0', 11079, 'Honda', 'CBR600RR', 160000, 'MOTO', '/images/coches/15.jpg'),
(16, 2021, 'rojo', 'Panigale V2 de alta calidad y gran rendimiento. Ideal para quienes buscan moto con carácter y fiabilidad.', b'1', 23920, 'Ducati', 'Panigale V2', 109053, 'MOTO', '/images/coches/16.jpg'),
(18, 2022, 'blanco', 'S1000RR de alta calidad y gran rendimiento. Ideal para quienes buscan moto con carácter y fiabilidad.', b'1', 19055, 'BMW', 'S1000RR', 66699, 'MOTO', '/images/coches/18.jpg'),
(20, 2024, 'azul', 'Sportsman 570 de alta calidad y gran rendimiento. Ideal para quienes buscan cuad con carácter y fiabilidad.', b'1', 10586, 'Polaris', 'Sportsman 570', 105498, 'CUAD', '/images/coches/20.jpg'),
(21, 2024, 'azul', 'Grizzly 700 de alta calidad y gran rendimiento. Ideal para quienes buscan cuad con carácter y fiabilidad.', b'1', 23810, 'Yamaha', 'Grizzly 700', 123025, 'CUAD', '/images/coches/21.jpg'),
(22, 2022, 'gris', 'CForce 600 de alta calidad y gran rendimiento. Ideal para quienes buscan cuad con carácter y fiabilidad.', b'1', 12977, 'CFMOTO', 'CForce 600', 37710, 'CUAD', '/images/coches/21.jpg'),
(23, 2023, 'rojo', 'KingQuad 750 de alta calidad y gran rendimiento. Ideal para quienes buscan cuad con carácter y fiabilidad.', b'1', 13210, 'Suzuki', 'KingQuad 750', 49021, 'CUAD', '/images/coches/22.jpg'),
(25, 2023, 'azul', 'GR Yaris de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 14395, 'Toyota', 'GR Yaris', 69520, 'COCHE', '/images/coches/25.jpg'),
(27, 2023, 'gris', 'i30 N de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 16484, 'Hyundai', 'i30 N', 63956, 'COCHE', '/images/coches/27.jpg'),
(28, 2024, 'gris', 'MX-5 de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 24044, 'Mazda', 'MX-5', 143717, 'COCHE', '/images/coches/28.jpg'),
(30, 2022, 'verde', 'Golf R de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 18380, 'Volkswagen', 'Golf R', 122763, 'COCHE', '/images/coches/30.jpg'),
(31, 2024, 'rojo', 'Mégane RS de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 26050, 'Renault', 'Mégane RS', 109166, 'COCHE', '/images/coches/31.jpg'),
(32, 2021, 'gris', '508 PSE de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 15533, 'Peugeot', '508 PSE', 108745, 'COCHE', '/images/coches/32.jpg'),
(33, 2022, 'verde', 'Leon VZ de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'0', 21859, 'Cupra', 'Leon VZ', 143292, 'COCHE', '/images/coches/33.jpg'),
(34, 2021, 'blanco', 'Octavia RS de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 11767, 'Skoda', 'Octavia RS', 67621, 'COCHE', '/images/coches/34.jpg'),
(36, 2021, 'rojo', 'RC F de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 18283, 'Lexus', 'RC F', 132578, 'COCHE', '/images/coches/36.jpg'),
(37, 2023, 'blanco', 'F-Type P300 de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 10792, 'Jaguar', 'F-Type P300', 137630, 'COCHE', '/images/coches/37.jpg'),
(38, 2023, 'rojo', 'Camaro SS de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'1', 12018, 'Chevrolet', 'Camaro SS', 69325, 'COCHE', '/images/coches/38.jpg'),
(39, 2022, 'gris', 'WRX STI de alta calidad y gran rendimiento. Ideal para quienes buscan coche con carácter y fiabilidad.', b'0', 19690, 'Subaru', 'WRX STI', 69661, 'COCHE', '/images/coches/39.jpg'),
(40, 2024, 'negro', 'SUV de alto rendimiento con un diseño imponente.', b'0', 20000, 'BMW', 'X5 M', 90000, 'COCHE', '/images/coches/40.jpg'),
(41, 2023, 'rojo', 'Deportivo de lujo con motor V8, 500 CV.', b'1', 15000, 'Audi', 'RS7', 115000, 'COCHE', '/images/coches/41.jpg'),
(42, 2024, 'blanco', 'Sedán eléctrico con autonomía de hasta 600 km.', b'1', 8000, 'Tesla', 'Model 3', 45000, 'COCHE', '/images/coches/42.jpg'),
(43, 2023, 'azul', 'Coupé de lujo con una experiencia de conducción inigualable.', b'1', 5000, 'Mercedes-Benz', 'AMG GT', 130000, 'COCHE', '/images/coches/43.jpg'),
(44, 2023, 'verde', 'Camioneta todo terreno de alta calidad y rendimiento.', b'1', 12000, 'Ford', 'F-150 Raptor', 65000, 'COCHE', '/images/coches/44.jpg'),
(45, 2022, 'gris', 'Deportivo icónico con 400 CV y tracción trasera.', b'1', 10000, 'Chevrolet', 'Corvette Stingray', 85000, 'COCHE', '/images/coches/45.jpg'),
(46, 2024, 'negro', 'Furgoneta eléctrica con capacidades de carga excepcionales.', b'1', 6000, 'Mercedes-Benz', 'eVito', 55000, 'COCHE', '/images/coches/46.jpg'),
(47, 2023, 'blanco', 'SUV híbrido con sistema de tracción total y motor eficiente.', b'1', 4000, 'Toyota', 'RAV4 Prime', 43000, 'COCHE', '/images/coches/47.jpg'),
(48, 2022, 'rojo', 'Coupé deportivo de lujo, ideal para los amantes de la velocidad.', b'1', 9000, 'BMW', 'M4', 105000, 'COCHE', '/images/coches/48.jpg'),
(49, 2024, 'gris', 'Monovolumen espacioso y elegante con tecnología avanzada.', b'1', 15000, 'Volkswagen', 'Sharan', 47000, 'COCHE', '/images/coches/49.jpg'),
(51, 2021, 'blanco', 'SUV eléctrico con un diseño futurista y gran autonomía.', b'1', 5000, 'Audi', 'Q4 e-tron', 59000, 'COCHE', '/images/coches/51.jpg'),
(53, 2024, 'negro', 'Camión de carga pesada con sistema de frenos mejorado.', b'1', 30000, 'Scania', 'S730', 120000, 'CAMION', '/images/coches/53.jpg'),
(54, 2023, 'rojo', 'Camión de reparto ligero con espacio eficiente.', b'1', 15000, 'Mercedes-Benz', 'Sprinter', 55000, 'CAMION', '/images/coches/54.jpg'),
(62, 2024, 'rojo', 'Moto deportiva con diseño agresivo y tecnología avanzada.', b'1', 12000, 'Honda', 'CBR1000RR', 23000, 'MOTO', '/images/coches/62.jpg'),
(63, 2023, 'azul', 'Moto naked de alta cilindrada con confort y rendimiento.', b'1', 15000, 'Kawasaki', 'Z1000', 15000, 'MOTO', '/images/coches/63.jpg'),
(64, 2024, 'negro', 'Moto trail para aventuras todo terreno, diseñada para largas distancias.', b'1', 9000, 'Suzuki', 'V-Strom 650', 11500, 'MOTO', '/images/coches/64.jpg'),
(65, 2024, 'blanco', 'Moto de estilo retro con gran confort y motor de 900cc.', b'1', 4000, 'BMW', 'R Nine T', 14500, 'MOTO', '/images/coches/65.jpg'),
(67, 2022, 'verde', 'Moto de competición con tecnología de punta y motor de 1000cc.', b'1', 8000, 'Kawasaki', 'Ninja ZX-10R', 18000, 'MOTO', '/images/coches/67.jpg'),
(68, 2024, 'gris', 'Quad todo terreno ideal para aventuras extremas.', b'1', 11000, 'Polaris', 'RZR XP 1000', 25000, 'CUAD', '/images/coches/68.jpg'),
(69, 2023, 'negro', 'Quad con sistema de tracción total y gran fiabilidad.', b'1', 7000, 'Can-Am', 'Outlander 1000R', 15500, 'CUAD', '/images/coches/69.jpg'),
(70, 2024, 'rojo', 'Quad deportivo con gran suspensión y motor potente.', b'1', 6000, 'Yamaha', 'Raptor 700R', 13500, 'CUAD', '/images/coches/70.jpg'),
(72, 2024, 'verde', 'Quad robusto y resistente, ideal para terrenos difíciles.', b'1', 12000, 'Can-Am', 'Renegade 1000R', 17000, 'CUAD', '/images/coches/72.jpg'),
(73, 2022, 'gris', 'Cuatrimoto de alto rendimiento con gran capacidad de carga.', b'1', 8000, 'Yamaha', 'Grizzly 700', 13500, 'CUAD', '/images/coches/73.jpg'),
(74, 2023, 'blanco', 'Cuatrimoto para todo tipo de terreno con sistema de tracción total.', b'1', 9500, 'Polaris', 'Sportsman 570', 14500, 'CUAD', '/images/coches/74.jpg'),
(75, 2024, 'rojo', 'Cuatrimoto ligera con gran control en senderos angostos.', b'1', 10500, 'Can-Am', 'Outlander 450', 12500, 'CUAD', '/images/coches/75.jpg'),
(76, 2024, 'negro', 'Cuatrimoto potente para terrenos difíciles, ideal para profesionales.', b'1', 11500, 'Yamaha', 'Kodiak 700', 15500, 'CUAD', '/images/coches/76.jpg'),
(77, 2022, 'azul', 'Cuatrimoto ideal para trabajos en campo y recreación.', b'1', 8500, 'Suzuki', 'KingQuad 750AXi', 16500, 'CUAD', '/images/coches/77.jpg'),
(79, 2024, 'gris', 'Cuatrimoto con excelente desempeño en terrenos de barro y agua.', b'1', 12000, 'Polaris', 'Sportsman 850', 19500, 'CUAD', '/images/coches/79.jpg'),
(80, 2023, 'rojo', 'Cuatrimoto ágil y dinámica, perfecta para rutas técnicas.', b'1', 14000, 'Yamaha', 'Raptor 700R', 20500, 'CUAD', '/images/coches/80.jpg'),
(81, 2024, 'negro', 'Camioneta pick-up de alto rendimiento, resistente y duradera.', b'1', 13000, 'Ford', 'F-250 Super Duty', 60000, 'COCHE', '/images/coches/81.jpg'),
(82, 2023, 'blanco', 'Camioneta de lujo con todas las comodidades y tecnología avanzada.', b'1', 14000, 'Ram', '1500 Limited', 85000, 'COCHE', '/images/coches/82.jpg'),
(83, 2024, 'gris', 'SUV familiar con gran espacio y seguridad.', b'1', 5000, 'Volvo', 'XC90', 85000, 'COCHE', '/images/coches/83.jpg'),
(84, 2022, 'rojo', 'SUV compacto con excelente maniobrabilidad y estilo urbano.', b'1', 15000, 'Hyundai', 'Kona', 28000, 'COCHE', '/images/coches/84.jpg'),
(85, 2024, 'blanco', 'SUV deportivo de alto rendimiento con motor eficiente.', b'1', 5000, 'BMW', 'X6', 95000, 'COCHE', '/images/coches/85.jpg'),
(88, 2022, 'gris', 'SUV compacto con excelente eficiencia de combustible.', b'0', 10000, 'Mazda', 'CX-5', 45000, 'COCHE', '/images/coches/88.jpg'),
(89, 2023, 'rojo', 'SUV compacto con excelente eficiencia de combustible.', b'1', 9000, 'Mazda ', 'CX-5', 47000, 'COCHE', '/images/coches/89.jpg'),
(90, 2024, 'negro', 'SUV de lujo con tracción total y gran seguridad.', b'1', 12000, 'Audi', 'Q7', 98000, 'COCHE', '/images/coches/90.jpg'),
(91, 2023, 'blanco', 'SUV eléctrico con autonomía excepcional y sistema de conducción autónoma.', b'1', 7000, 'BMW', 'iX', 115000, 'COCHE', '/images/coches/91.jpg'),
(92, 2024, 'verde', 'SUV ecológico de alto rendimiento y gran espacio interior.', b'1', 11000, 'Volvo', 'Xc60', 68000.02, 'COCHE', '/images/coches/92.jpg'),
(124, 0, 'red', 'dgdggdgdf', b'1', 4444, 'Mercedes-Benzd', 'Giulia ss', 444444, 'CUAD', '/images/coches/1748434970382.jpg');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `citasdeprueba`
--
ALTER TABLE `citasdeprueba`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK507h848ergu4isl1xpelopebg` (`idcliente`),
  ADD KEY `FKcvyvpabodjo4d9gjtpa61y75t` (`idvehiculo`),
  ADD KEY `FKqu7wewh91tvoda6jrrnq12s37` (`idcitadeprueba`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKlqi554ykre0smsfv9xk252u2u` (`idusuario`),
  ADD KEY `FK27mr3l01imgsbo7e0dxb5gvuo` (`vehiculo_id`);

--
-- Indices de la tabla `evaluacion`
--
ALTER TABLE `evaluacion`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKh9yjrsaxhyeb7hkm8f0rae65d` (`idcita`),
  ADD KEY `FKebqeuccxi3vhi6s5sg3bcyyit` (`idcliente`),
  ADD KEY `FKaj3xvox65plq4ryo3crq2rrqs` (`idvehiculo`),
  ADD KEY `FKc77c140nys4qrfwud0dduo9ph` (`idevaluacion`),
  ADD KEY `FKplbf75qg1fu30ixner1sknpa` (`idcitadeprueba`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `vehiculos`
--
ALTER TABLE `vehiculos`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `citasdeprueba`
--
ALTER TABLE `citasdeprueba`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `evaluacion`
--
ALTER TABLE `evaluacion`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=79;

--
-- AUTO_INCREMENT de la tabla `vehiculos`
--
ALTER TABLE `vehiculos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=126;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `citasdeprueba`
--
ALTER TABLE `citasdeprueba`
  ADD CONSTRAINT `FK507h848ergu4isl1xpelopebg` FOREIGN KEY (`idcliente`) REFERENCES `cliente` (`id`),
  ADD CONSTRAINT `FKcvyvpabodjo4d9gjtpa61y75t` FOREIGN KEY (`idvehiculo`) REFERENCES `vehiculos` (`id`),
  ADD CONSTRAINT `FKqu7wewh91tvoda6jrrnq12s37` FOREIGN KEY (`idcitadeprueba`) REFERENCES `vehiculos` (`id`);

--
-- Filtros para la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD CONSTRAINT `FK27mr3l01imgsbo7e0dxb5gvuo` FOREIGN KEY (`vehiculo_id`) REFERENCES `vehiculos` (`id`),
  ADD CONSTRAINT `FK7rvuuhwxpbrssngqkoqr888qs` FOREIGN KEY (`idusuario`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `evaluacion`
--
ALTER TABLE `evaluacion`
  ADD CONSTRAINT `FK1turlwuqqhlv3xdytddg54pho` FOREIGN KEY (`idcita`) REFERENCES `citasdeprueba` (`id`),
  ADD CONSTRAINT `FKaj3xvox65plq4ryo3crq2rrqs` FOREIGN KEY (`idvehiculo`) REFERENCES `vehiculos` (`id`),
  ADD CONSTRAINT `FKc77c140nys4qrfwud0dduo9ph` FOREIGN KEY (`idevaluacion`) REFERENCES `vehiculos` (`id`),
  ADD CONSTRAINT `FKebqeuccxi3vhi6s5sg3bcyyit` FOREIGN KEY (`idcliente`) REFERENCES `cliente` (`id`),
  ADD CONSTRAINT `FKplbf75qg1fu30ixner1sknpa` FOREIGN KEY (`idcitadeprueba`) REFERENCES `cliente` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
