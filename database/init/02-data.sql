INSERT INTO tercero (id, tipo_documento, numero_documento, tipo_persona, nombre_razon_social, email, celular, acepta_notificaciones) VALUES
(1, 'CC',  '1012345678', 'NATURAL',  'Ana María Pérez',                 'ana.perez@correo.com',      '3001234567', TRUE),
(2, 'CC',  '79876543',   'NATURAL',  'Luis Gómez',                      'luis.gomez@correo.com',     '3109876543', TRUE),
(3, 'NIT', '900123456',  'JURIDICA', 'Inmobiliaria Los Andes S.A.S.',   'contacto@losandes.com',     '6015551234', TRUE),
(4, 'CC',  '1020304050', 'NATURAL',  'Carlos Rodríguez',                'carlos.rodriguez@correo.com','3201112233', TRUE),
(5, 'CC',  '52345678',   'NATURAL',  'Marta Salazar',                   'marta.salazar@correo.com',  '3157778899', TRUE),
(6, 'CE',  '456789',     'NATURAL',  'Jorge Herrera',                   'jorge.herrera@correo.com',  '3184445566', FALSE),
(7, 'CC',  '80123456',   'NATURAL',  'Patricia Mejía',                  'patricia.mejia@correo.com', '3016667788', TRUE),
(8, 'NIT', '901987654',  'JURIDICA', 'Administración Edificio Central', 'admin@edificiocentral.com', '6017778899', TRUE),
(9, 'CC',  '1098765432', 'NATURAL',  'Sofía Ramírez',                   'sofia.ramirez@correo.com',  '3023334455', TRUE);

INSERT INTO inmueble (id, matricula_inmobiliaria, direccion, ciudad, tipo) VALUES
(1, '50C-1234567', 'Cra 7 # 32-10 Apto 501',      'Bogotá',   'VIVIENDA'),
(2, '50N-2345678', 'Calle 100 # 15-20 Apto 302',  'Bogotá',   'VIVIENDA'),
(3, '001-3456789', 'Cra 43A # 1-50 Local 105',    'Medellín', 'COMERCIAL'),
(4, '370-4567890', 'Av. 6N # 23-45 Apto 801',     'Cali',     'VIVIENDA'),
(5, '50S-5678901', 'Calle 26 Sur # 50-12 Casa 4', 'Bogotá',   'VIVIENDA');

INSERT INTO poliza (id, numero_poliza, tipo, tomador_id, fecha_inicio, fecha_fin, meses_vigencia, canon_mensual, valor_prima, estado, renovacion_automatica, numero_renovacion, estado_sync_core, numero_poliza_core) VALUES
(1, 'IND-2026-0001', 'INDIVIDUAL', 1, '2026-01-01', '2027-01-01', 12, 2000000.00, 24000000.00, 'VIGENTE',   TRUE, 0, 'SINCRONIZADO', 'CORE-880001'),
(2, 'COL-2026-0001', 'COLECTIVA',  3, '2026-02-01', '2027-02-01', 12, 4700000.00, 56400000.00, 'VIGENTE',   TRUE, 0, 'SINCRONIZADO', 'CORE-880002'),
(3, 'IND-2025-0002', 'INDIVIDUAL', 6, '2025-06-01', '2026-06-01', 12, 1800000.00, 21600000.00, 'CANCELADA', TRUE, 0, 'SINCRONIZADO', 'CORE-770003'),
(4, 'COL-2026-0002', 'COLECTIVA',  8, '2026-03-01', '2026-09-01',  6, 2500000.00, 15000000.00, 'VIGENTE',   TRUE, 0, 'SINCRONIZADO', 'CORE-880004');

INSERT INTO riesgo (id, poliza_id, inmueble_id, asegurado_id, beneficiario_id, canon_mensual, valor_prima, fecha_inclusion, fecha_cancelacion, motivo_cancelacion, estado, estado_sync_core) VALUES
(1, 1, 1, 1, 2, 2000000.00, 24000000.00, '2026-01-01', NULL,         NULL,                       'ACTIVO',    'SINCRONIZADO'),
(2, 2, 2, 4, 5, 1500000.00, 18000000.00, '2026-02-01', NULL,         NULL,                       'ACTIVO',    'SINCRONIZADO'),
(3, 2, 3, 7, 5, 3200000.00, 38400000.00, '2026-02-01', NULL,         NULL,                       'ACTIVO',    'SINCRONIZADO'),
(4, 3, 4, 6, 2, 1800000.00, 21600000.00, '2025-06-01', '2025-11-15', 'Terminación del contrato', 'CANCELADO', 'SINCRONIZADO'),
(5, 4, 5, 9, 7, 2500000.00, 15000000.00, '2026-03-01', NULL,         NULL,                       'ACTIVO',    'SINCRONIZADO');

INSERT INTO certificado (id, poliza_id, numero_certificado, tipo_movimiento, fecha_inicio, fecha_fin, canon_mensual, valor_prima, ipc_aplicado, fecha_registro) VALUES
(1, 1, 1, 'EXPEDICION', '2026-01-01', '2027-01-01', 2000000.00, 24000000.00, NULL, '2025-12-20 10:00:00'),
(2, 2, 1, 'EXPEDICION', '2026-02-01', '2027-02-01', 4700000.00, 56400000.00, NULL, '2026-01-25 09:30:00'),
(3, 3, 1, 'EXPEDICION', '2025-06-01', '2026-06-01', 1800000.00, 21600000.00, NULL, '2025-05-28 15:45:00'),
(4, 4, 1, 'EXPEDICION', '2026-03-01', '2026-09-01', 2500000.00, 15000000.00, NULL, '2026-02-26 11:15:00');

SELECT setval(pg_get_serial_sequence('tercero', 'id'), (SELECT MAX(id) FROM tercero));
SELECT setval(pg_get_serial_sequence('inmueble', 'id'), (SELECT MAX(id) FROM inmueble));
SELECT setval(pg_get_serial_sequence('poliza', 'id'), (SELECT MAX(id) FROM poliza));
SELECT setval(pg_get_serial_sequence('riesgo', 'id'), (SELECT MAX(id) FROM riesgo));
SELECT setval(pg_get_serial_sequence('certificado', 'id'), (SELECT MAX(id) FROM certificado));
