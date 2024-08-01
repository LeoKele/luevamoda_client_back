-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 01-08-2024 a las 22:00:15
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
-- Base de datos: `lueva_moda`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria_productos`
--

CREATE TABLE `categoria_productos` (
  `id_categoria` int(11) NOT NULL,
  `descripcion_categoria` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `categoria_productos`
--

INSERT INTO `categoria_productos` (`id_categoria`, `descripcion_categoria`) VALUES
(1, 'Remeras'),
(2, 'Pantalones');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

CREATE TABLE `clientes` (
  `id_cliente` int(11) NOT NULL,
  `nombre` text NOT NULL,
  `telefono` text NOT NULL,
  `mail` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`id_cliente`, `nombre`, `telefono`, `mail`) VALUES
(1, 'Juan', '1155667788', 'juan@gmail.com'),
(3, 'Francisco', '1123456688', 'francisco@gmail.com'),
(4, 'Pepito', '113344556677', 'juan@gmail.com'),
(5, 'Otto', '1123456788', 'otto@gmail.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `gastos`
--

CREATE TABLE `gastos` (
  `id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `descripcion` text NOT NULL,
  `costo` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `gastos`
--

INSERT INTO `gastos` (`id`, `fecha`, `descripcion`, `costo`) VALUES
(1, '2024-07-30', 'x10 rollos cartulina', 35000.00),
(4, '2024-08-01', 'x15 rollos cartulina', 40000.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `imagenes_productos`
--

CREATE TABLE `imagenes_productos` (
  `id` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `img_path` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `imagenes_productos`
--

INSERT INTO `imagenes_productos` (`id`, `id_producto`, `img_path`) VALUES
(1, 1, '1_1.png'),
(2, 1, '1_2.png'),
(3, 1, '1_3.png'),
(11, 12, '12_1.png'),
(12, 12, '12_2.png'),
(13, 12, '12_3.png'),
(16, 13, '13_2.png'),
(17, 13, '13_3.png'),
(18, 15, '15_1.png');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id_pedido` int(11) NOT NULL,
  `fecha_recibido` date NOT NULL,
  `id_cliente` int(11) DEFAULT NULL,
  `fecha_finalizado` date DEFAULT NULL,
  `descripcion` text NOT NULL,
  `estado` enum('Pendiente','Procesando','Completado','Cancelado') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id_pedido`, `fecha_recibido`, `id_cliente`, `fecha_finalizado`, `descripcion`, `estado`) VALUES
(3, '2024-07-25', 3, '2024-08-31', 'Realizado de moldes de campera de Bebe', ''),
(4, '2024-07-30', 3, NULL, 'Moldes para jeans mujer', 'Pendiente'),
(5, '2024-07-31', 1, '2024-08-01', 'Molde campera jean', 'Cancelado'),
(6, '2024-07-31', 4, NULL, 'Molde de remera mujer', 'Procesando'),
(7, '2024-07-15', 1, '2024-07-31', 'Campera rompeviento', 'Completado'),
(8, '2024-08-01', 5, '2024-08-02', 'Camperas jean', 'Completado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL,
  `nombre_producto` text NOT NULL,
  `cliente` text DEFAULT NULL,
  `id_categoria` int(11) DEFAULT 0,
  `precio_molde_base` decimal(10,2) DEFAULT 0.00,
  `precio_molde_digital` decimal(10,2) DEFAULT 0.00,
  `precio_molde_cartulina` decimal(10,2) DEFAULT 0.00,
  `listado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id_producto`, `nombre_producto`, `cliente`, `id_categoria`, `precio_molde_base`, `precio_molde_digital`, `precio_molde_cartulina`, `listado`) VALUES
(1, 'Remera Bebé', 'Pedro', 1, 3000.00, 4500.00, 7000.00, 1),
(5, 'Remera Bebe', 'Francisco', 2, 5000.00, 6000.00, 9000.00, 0),
(12, 'Campera Rompeviento', 'Nisca', 1, 2500.00, 5000.00, 7000.00, 1),
(13, 'Pantalon deportivo', 'Nike', 2, 2500.00, 4500.00, 5000.00, 0),
(15, 'Remera Jean', 'Otto', 1, 2600.00, 5700.00, 6500.25, 1),
(16, 'Pantalon Jean', 'Popi', 2, 2500.00, 3500.00, 4765.00, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `stock`
--

CREATE TABLE `stock` (
  `id_stock` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `stock`
--

INSERT INTO `stock` (`id_stock`, `id_producto`, `cantidad`) VALUES
(1, 1, 4),
(4, 5, 3),
(10, 12, 8),
(11, 13, 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `talles_productos`
--

CREATE TABLE `talles_productos` (
  `id` int(11) NOT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `talle` enum('XXS','XS','S','M','L','XL','XXL') DEFAULT NULL,
  `medida_busto` decimal(5,2) DEFAULT NULL,
  `medida_cintura` decimal(5,2) DEFAULT NULL,
  `medida_cadera` decimal(5,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `talles_productos`
--

INSERT INTO `talles_productos` (`id`, `id_producto`, `talle`, `medida_busto`, `medida_cintura`, `medida_cadera`) VALUES
(1, 1, 'XS', 6.00, 10.00, 25.00),
(2, 1, 'S', 15.00, 20.00, 25.00),
(3, 1, 'L', 25.00, 30.00, 55.00),
(5, 1, 'XXS', 4.00, 8.00, 20.00),
(9, 15, 'L', 24.00, 45.00, 35.00),
(10, 15, 'XXS', 2.00, 5.00, 12.00),
(12, 16, 'XXL', 22.00, 33.00, 44.00),
(13, 16, 'M', 23.00, 25.00, 40.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventas`
--

CREATE TABLE `ventas` (
  `id_venta` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `fecha_venta` date NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ventas`
--

INSERT INTO `ventas` (`id_venta`, `id_cliente`, `id_producto`, `fecha_venta`, `cantidad`, `precio_unitario`) VALUES
(3, 3, 1, '2024-05-30', 10, 5200.00),
(4, 4, 5, '2024-05-30', 2, 8000.00),
(5, 1, 5, '2024-07-30', 7, 1000.00),
(6, 3, 12, '2024-08-01', 10, 3600.00),
(7, 4, 13, '2024-07-30', 3, 6000.00);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categoria_productos`
--
ALTER TABLE `categoria_productos`
  ADD PRIMARY KEY (`id_categoria`),
  ADD UNIQUE KEY `categoria_unica` (`descripcion_categoria`) USING HASH;

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`id_cliente`),
  ADD UNIQUE KEY `cliente_unico` (`nombre`) USING HASH;

--
-- Indices de la tabla `gastos`
--
ALTER TABLE `gastos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `imagenes_productos`
--
ALTER TABLE `imagenes_productos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `img_unica` (`img_path`),
  ADD KEY `id_producto` (`id_producto`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`id_pedido`),
  ADD KEY `id_cliente` (`id_cliente`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id_producto`),
  ADD KEY `fk_id_categoria` (`id_categoria`);

--
-- Indices de la tabla `stock`
--
ALTER TABLE `stock`
  ADD PRIMARY KEY (`id_stock`),
  ADD UNIQUE KEY `Unique idProducto` (`id_producto`) USING BTREE;

--
-- Indices de la tabla `talles_productos`
--
ALTER TABLE `talles_productos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id_producto` (`id_producto`,`talle`);

--
-- Indices de la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD PRIMARY KEY (`id_venta`),
  ADD KEY `fk_id_producto_ventas` (`id_producto`),
  ADD KEY `id_cliente` (`id_cliente`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categoria_productos`
--
ALTER TABLE `categoria_productos`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `clientes`
--
ALTER TABLE `clientes`
  MODIFY `id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `gastos`
--
ALTER TABLE `gastos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `imagenes_productos`
--
ALTER TABLE `imagenes_productos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `id_pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `stock`
--
ALTER TABLE `stock`
  MODIFY `id_stock` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `talles_productos`
--
ALTER TABLE `talles_productos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `ventas`
--
ALTER TABLE `ventas`
  MODIFY `id_venta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `imagenes_productos`
--
ALTER TABLE `imagenes_productos`
  ADD CONSTRAINT `imagenes_productos_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`);

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`);

--
-- Filtros para la tabla `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `fk_id_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_productos` (`id_categoria`);

--
-- Filtros para la tabla `stock`
--
ALTER TABLE `stock`
  ADD CONSTRAINT `fk_id_producto` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`);

--
-- Filtros para la tabla `talles_productos`
--
ALTER TABLE `talles_productos`
  ADD CONSTRAINT `talles_productos_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`);

--
-- Filtros para la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD CONSTRAINT `fk_id_producto_ventas` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  ADD CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
