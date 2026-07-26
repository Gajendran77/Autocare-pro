-- =========================================================
-- AutoCare Pro - Vehicle Service Management System
-- Complete MySQL Schema
-- =========================================================

CREATE DATABASE IF NOT EXISTS autocare_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE autocare_pro;

-- ---------------------------------------------------------
-- USERS  (Admin / Customer / Mechanic)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name           VARCHAR(120)  NOT NULL,
    email               VARCHAR(150)  NOT NULL UNIQUE,
    password            VARCHAR(255)  NOT NULL,
    phone               VARCHAR(20),
    role                ENUM('ADMIN','CUSTOMER','MECHANIC') NOT NULL,
    profile_image       VARCHAR(255),
    is_enabled          BOOLEAN       NOT NULL DEFAULT TRUE,
    specialization      VARCHAR(255),
    reset_token         VARCHAR(255),
    reset_token_expiry  DATETIME,
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- VEHICLES
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicles (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id              BIGINT NOT NULL,
    vehicle_type          ENUM('CAR','BIKE','TRUCK') NOT NULL,
    brand                 VARCHAR(60) NOT NULL,
    model                 VARCHAR(60) NOT NULL,
    manufacture_year      INT,
    registration_number   VARCHAR(30) NOT NULL UNIQUE,
    fuel_type             ENUM('PETROL','DIESEL','ELECTRIC','CNG','HYBRID') DEFAULT 'PETROL',
    image_url             VARCHAR(500),
    status                VARCHAR(30) DEFAULT 'ACTIVE',
    next_service_due      DATE,
    last_service_date     DATE,
    created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicles_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_vehicles_owner (owner_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- BOOKINGS
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code         VARCHAR(20) UNIQUE,
    customer_id          BIGINT NOT NULL,
    vehicle_id           BIGINT,
    mechanic_id          BIGINT,
    vehicle_type         ENUM('CAR','BIKE','TRUCK') NOT NULL,
    owner_name           VARCHAR(120) NOT NULL,
    phone                VARCHAR(20) NOT NULL,
    vehicle_number       VARCHAR(30) NOT NULL,
    brand                VARCHAR(60) NOT NULL,
    model                VARCHAR(60) NOT NULL,
    manufacture_year     INT,
    problem_description  TEXT,
    preferred_date       DATE NOT NULL,
    pickup_required      BOOLEAN DEFAULT FALSE,
    status               ENUM('PENDING','CONFIRMED','ASSIGNED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    progress_percent     INT DEFAULT 0,
    service_notes        TEXT,
    estimated_cost       DECIMAL(10,2),
    final_cost           DECIMAL(10,2),
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME,
    completed_at         DATETIME,
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_vehicle  FOREIGN KEY (vehicle_id)  REFERENCES vehicles(id) ON DELETE SET NULL,
    CONSTRAINT fk_bookings_mechanic FOREIGN KEY (mechanic_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_bookings_customer (customer_id),
    INDEX idx_bookings_mechanic (mechanic_id),
    INDEX idx_bookings_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- CHECKLIST ITEMS (Mechanic repair checklist per booking)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS checklist_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id   BIGINT NOT NULL,
    description  VARCHAR(200) NOT NULL,
    completed    BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_checklist_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- SERVICE IMAGES (Before / After photos)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS service_images (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id   BIGINT NOT NULL,
    image_url    VARCHAR(500) NOT NULL,
    type         VARCHAR(10),  -- BEFORE / AFTER
    uploaded_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_images_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- INVOICES
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS invoices (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL UNIQUE,
    invoice_number  VARCHAR(30) UNIQUE,
    subtotal        DECIMAL(10,2),
    tax             DECIMAL(10,2),
    total_amount    DECIMAL(10,2),
    pdf_path        VARCHAR(500),
    issued_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- PAYMENTS
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    method          VARCHAR(30),   -- CARD, UPI, CASH, NET_BANKING
    status          ENUM('PENDING','PAID','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    transaction_id  VARCHAR(60),
    paid_at         DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- NOTIFICATIONS
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    message     VARCHAR(255) NOT NULL,
    type        VARCHAR(30),
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- INVENTORY ITEMS (Spare parts / stock)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS inventory_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(120) NOT NULL,
    category        VARCHAR(60),
    stock_quantity  INT DEFAULT 0,
    unit_price      DECIMAL(10,2),
    reorder_level   INT DEFAULT 5,
    status          VARCHAR(30) DEFAULT 'IN_STOCK'
) ENGINE=InnoDB;
