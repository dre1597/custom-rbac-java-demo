CREATE TABLE users (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  password VARCHAR(255),
  status VARCHAR(255),
  created_at TIMESTAMP WITH TIME ZONE,
  updated_at TIMESTAMP WITH TIME ZONE,
  role_id UUID,
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);
