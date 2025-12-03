GRANT ALL PRIVILEGES ON smart_career.* TO 'root'@'%' IDENTIFIED BY 'rahul';
FLUSH PRIVILEGES;

USE smart_career;

CREATE TABLE role_entity (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO role_entity (name) VALUES ('USER');
