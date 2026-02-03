CREATE TABLE user_locations (
    id BIGSERIAL PRIMARY KEY,
    
    ip VARCHAR(45) NOT NULL,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
