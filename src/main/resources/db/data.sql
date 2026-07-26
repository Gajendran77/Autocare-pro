-- =========================================================
-- AutoCare Pro - Seed / Demo Data
-- NOTE: passwords below are BCrypt hashes of "Password@123"
-- =========================================================
USE autocare_pro;

-- Users: 1 Admin, 2 Customers, 2 Mechanics
INSERT INTO users (full_name, email, password, phone, role, is_enabled, specialization) VALUES
('Arjun Mehta',   'admin@autocarepro.com',    '$2a$12$3s1n1E8gk8m2r1eYFqjKfeUOx9c6f6l6Q1KX5FhU2i7lU8bYqjM4G', '9990001111', 'ADMIN', TRUE, NULL),
('Rohan Sharma',  'customer@autocarepro.com', '$2a$12$3s1n1E8gk8m2r1eYFqjKfeUOx9c6f6l6Q1KX5FhU2i7lU8bYqjM4G', '9990002222', 'CUSTOMER', TRUE, NULL),
('Priya Nair',    'priya@autocarepro.com',    '$2a$12$3s1n1E8gk8m2r1eYFqjKfeUOx9c6f6l6Q1KX5FhU2i7lU8bYqjM4G', '9990003333', 'CUSTOMER', TRUE, NULL),
('Vikram Singh',  'mechanic@autocarepro.com', '$2a$12$3s1n1E8gk8m2r1eYFqjKfeUOx9c6f6l6Q1KX5FhU2i7lU8bYqjM4G', '9990004444', 'MECHANIC', TRUE, 'Engine, Electrical'),
('Karan Verma',   'karan@autocarepro.com',    '$2a$12$3s1n1E8gk8m2r1eYFqjKfeUOx9c6f6l6Q1KX5FhU2i7lU8bYqjM4G', '9990005555', 'MECHANIC', TRUE, 'Bodywork, Suspension');

-- Vehicles for Rohan Sharma (id=2)
INSERT INTO vehicles (owner_id, vehicle_type, brand, model, manufacture_year, registration_number, fuel_type, image_url, next_service_due) VALUES
(2, 'CAR', 'BMW', '3 Series', 2022, 'DL01AB1234', 'PETROL', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&w=800&q=80', DATE_ADD(CURDATE(), INTERVAL 3 MONTH)),
(2, 'BIKE', 'Ducati', 'Panigale V4', 2023, 'DL05XY9988', 'PETROL', 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80', DATE_ADD(CURDATE(), INTERVAL 1 MONTH));

-- Vehicles for Priya Nair (id=3)
INSERT INTO vehicles (owner_id, vehicle_type, brand, model, manufacture_year, registration_number, fuel_type, image_url, next_service_due) VALUES
(3, 'TRUCK', 'Tata', 'Prima', 2021, 'MH12CD5566', 'DIESEL', 'https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?auto=format&fit=crop&w=800&q=80', DATE_ADD(CURDATE(), INTERVAL 2 MONTH));

-- Sample bookings
INSERT INTO bookings (booking_code, customer_id, vehicle_id, mechanic_id, vehicle_type, owner_name, phone, vehicle_number, brand, model, manufacture_year, problem_description, preferred_date, pickup_required, status, progress_percent, estimated_cost) VALUES
('ACP-100001', 2, 1, 4, 'CAR', 'Rohan Sharma', '9990002222', 'DL01AB1234', 'BMW', '3 Series', 2022, 'Engine oil change and brake inspection', CURDATE(), FALSE, 'IN_PROGRESS', 60, 4500.00),
('ACP-100002', 3, 3, NULL, 'TRUCK', 'Priya Nair', '9990003333', 'MH12CD5566', 'Tata', 'Prima', 2021, 'Suspension noise and AC not cooling', DATE_ADD(CURDATE(), INTERVAL 2 DAY), TRUE, 'PENDING', 0, 8000.00);

-- Inventory
INSERT INTO inventory_items (name, category, stock_quantity, unit_price, reorder_level, status) VALUES
('Engine Oil (5L)', 'ENGINE', 40, 2200.00, 10, 'IN_STOCK'),
('Brake Pads (Set)', 'BRAKE', 25, 1800.00, 5, 'IN_STOCK'),
('Air Filter', 'ENGINE', 3, 650.00, 5, 'LOW_STOCK'),
('Battery 12V', 'ELECTRICAL', 12, 5200.00, 4, 'IN_STOCK'),
('Tyre (Truck)', 'TYRE', 0, 12000.00, 4, 'OUT_OF_STOCK');
