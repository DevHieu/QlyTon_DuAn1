DROP DATABASE IF EXISTS QLyTon;
CREATE DATABASE QLyTon;
USE QLyTon;

CREATE TABLE Users (
    Username VARCHAR(50) NOT NULL PRIMARY KEY,
    Fullname VARCHAR(100) NOT NULL,
    Password VARCHAR(100) NOT NULL,
    Manager BOOLEAN NOT NULL,
    Photo VARCHAR(255) NOT NULL,
    PhoneNumber VARCHAR(20) NOT NULL,
    Enabled BOOLEAN NOT NULL
);

-- 2. ProductType Table
CREATE TABLE ProductType (
    Id VARCHAR(20) NOT NULL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Unit VARCHAR(100) NOT NULL,
    HasThickness BOOLEAN NOT NULL DEFAULT 0,
    RequiresSize BOOLEAN NOT NULL DEFAULT 0,
    DefaultLength FLOAT NULL
);

-- 3. Thickness Table
CREATE TABLE Thickness (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Thick VARCHAR(10) NOT NULL,
    TypeId VARCHAR(20) NOT NULL,
    FOREIGN KEY (TypeId) REFERENCES ProductType(Id) ON DELETE CASCADE
);

-- 4. Products Table (CÓ THÊM COSTPRICE)
CREATE TABLE Products (
    Id VARCHAR(20) NOT NULL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Photo VARCHAR(255) NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice FLOAT NOT NULL,
    CostPrice FLOAT NOT NULL DEFAULT 0,  -- THÊM MỚI: Giá nhập
    Discount FLOAT NOT NULL,
    TypeId VARCHAR(20) NOT NULL,
    ThickID INT NULL,
    FOREIGN KEY (TypeId) REFERENCES ProductType(Id) ON DELETE RESTRICT,
    FOREIGN KEY (ThickID) REFERENCES Thickness(Id) ON DELETE SET NULL
);

-- 5. Customers Table
CREATE TABLE Customers (
    PhoneNumber VARCHAR(11) NOT NULL PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Address VARCHAR(100) NOT NULL
);

-- 6. Bills Table
CREATE TABLE Bills (
    Id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(50) NOT NULL,
    CustomerId VARCHAR(11) NOT NULL,
    Checkin DATETIME NOT NULL,
    Checkout DATETIME NULL,
    Note VARCHAR(500),
    Discount FLOAT NOT NULL,
    Deposit FLOAT NOT NULL,
    Status INT NOT NULL,
    FOREIGN KEY (Username) REFERENCES Users(Username) ON DELETE RESTRICT,
    FOREIGN KEY (CustomerId) REFERENCES Customers(PhoneNumber) ON DELETE RESTRICT
);

-- 7. BillDetails Table (CÓ THÊM COSTPRICE)
CREATE TABLE BillDetails (
    Id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    BillId BIGINT NOT NULL,
    ProductId VARCHAR(20) NOT NULL,
    UnitPrice FLOAT NOT NULL,
    CostPrice FLOAT NOT NULL DEFAULT 0,  -- THÊM MỚI: Giá nhập tại thời điểm bán
    Discount FLOAT NOT NULL,
    Quantity INT NOT NULL,
    Length FLOAT NULL,
    FOREIGN KEY (BillId) REFERENCES Bills(Id) ON DELETE CASCADE,
    FOREIGN KEY (ProductId) REFERENCES Products(Id) ON DELETE RESTRICT
);

-- 8. ProductPriceHistory Table
CREATE TABLE ProductPriceHistory (
    Id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ProductId VARCHAR(20) NOT NULL,
    CostPrice FLOAT NOT NULL,
    UnitPrice FLOAT NOT NULL,
    EffectiveDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductId) REFERENCES Products(Id) ON DELETE CASCADE
);

-- ==========================
-- TRIGGER check RequiresSize
-- ==========================
DELIMITER $$

CREATE TRIGGER trg_check_length_insert
BEFORE INSERT ON BillDetails
FOR EACH ROW
BEGIN
    DECLARE requires_size BOOLEAN;

    SELECT RequiresSize INTO requires_size
    FROM ProductType
    WHERE Id = (SELECT TypeId FROM Products WHERE Id = NEW.ProductId);

    IF requires_size AND NEW.Length IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This product type requires Length.';
    END IF;
END$$

CREATE TRIGGER trg_check_length_update
BEFORE UPDATE ON BillDetails
FOR EACH ROW
BEGIN
    DECLARE requires_size BOOLEAN;

    SELECT RequiresSize INTO requires_size
    FROM ProductType
    WHERE Id = (SELECT TypeId FROM Products WHERE Id = NEW.ProductId);

    IF requires_size AND NEW.Length IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This product type requires Length.';
    END IF;
END$$

DELIMITER ;

-- ==========================
-- Stored Procedures
-- ==========================
DELIMITER //
CREATE PROCEDURE ImportProduct(
    IN p_id VARCHAR(20),
    IN p_quantity INT
)
BEGIN
    UPDATE Products 
    SET Quantity = Quantity + p_quantity
    WHERE Id = p_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE SellProduct(
    IN p_id VARCHAR(20),
    IN p_quantity INT
)
BEGIN
    DECLARE current_quantity INT;

    SELECT Quantity INTO current_quantity
    FROM Products
    WHERE Id = p_id;

    IF current_quantity < p_quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Not enough stock to sell!';
    ELSE
        UPDATE Products 
        SET Quantity = Quantity - p_quantity
        WHERE Id = p_id;
    END IF;
END //
DELIMITER ;

-- USERS
INSERT INTO Users VALUES
('admin', 'Nguyễn Văn A', '123456', true, 'admin.jpg', '0909123456', true),
('user01', 'Trần Thị B', '123456', false, 'user01.jpg', '0911123456', true);

-- PRODUCT TYPES
INSERT INTO ProductType VALUES
('TON', 'Tôn lạnh', 'Tấm', 1, 1, 6.0),
('XAGO', 'Xà gồ C', 'Thanh', 1, 1, 6.0),
('PHU', 'Phụ kiện', 'Cái', 0, 0, NULL);

-- THICKNESS
INSERT INTO Thickness (Thick, TypeId) VALUES
('3.0', 'TON'),
('4.5', 'TON'),
('1.5', 'XAGO'),
('2.0', 'XAGO');

-- PRODUCTS
INSERT INTO Products VALUES
('TON001', 'Tôn lạnh Hoa Sen 3.0', 'ton1.jpg', 100, 125000, 100000, 0, 'TON', 1),
('TON002', 'Tôn lạnh Đông Á 4.5', 'ton2.jpg', 80, 130000, 105000, 0, 'TON', 2),
('XG001', 'Xà gồ C 1.5 ly', 'xg1.jpg', 60, 150000, 120000, 0, 'XAGO', 3),
('XG002', 'Xà gồ C 2.0 ly', 'xg2.jpg', 50, 170000, 135000, 0, 'XAGO', 4),
('PK001', 'Ốc vít Inox', 'pk1.jpg', 500, 2000, 1500, 0, 'PHU', NULL);

-- CUSTOMERS
INSERT INTO Customers VALUES
('0909111222', 'Lê Văn Hùng', '123 Nguyễn Văn Cừ, Q5'),
('0911222333', 'Phạm Thị Mai', '456 Lê Lợi, Q1');

-- BILLS
INSERT INTO Bills (Username, CustomerId, Checkin, Checkout, Note, Discount, Deposit, Status) VALUES
('admin', '0909111222', NOW(), NULL, 'Khách thân thiết', 0, 500000, 0),
('user01', '0911222333', NOW(), NOW(), 'Giao ngay trong ngày', 50000, 1000000, 1);

-- BILL DETAILS
INSERT INTO BillDetails (BillId, ProductId, UnitPrice, CostPrice, Discount, Quantity, Length) VALUES
(1, 'TON001', 125000, 100000, 0, 10, 6.0),
(1, 'PK001', 2000, 1500, 0, 100, NULL),
(2, 'XG001', 150000, 120000, 5000, 20, 6.0),
(2, 'TON002', 130000, 105000, 0, 15, 6.0);

-- OPTIONAL: PRODUCT PRICE HISTORY
INSERT INTO ProductPriceHistory (ProductId, CostPrice, UnitPrice) VALUES
('TON001', 95000, 120000),
('TON001', 100000, 125000),
('TON002', 102000, 130000),
('XG001', 118000, 145000),
('XG001', 120000, 150000);

UPDATE Bills
SET Checkout = NOW(), Status = 1
WHERE Id IN (1, 2);
SELECT * FROM Bills WHERE Status = 1 AND Checkout IS NOT NULL;