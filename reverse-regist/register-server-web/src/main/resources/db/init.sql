CREATE TABLE IF NOT EXISTS t_appname_path(
  id INT AUTO_INCREMENT PRIMARY KEY,
  app_name VARCHAR(50) NOT NULL,
  mapping_path VARCHAR(100) NOT NULL,
  server_path VARCHAR(100) NOT NULL,
  create_time TIMESTAMP,
  unique index uniq_mapping_path(mapping_path)
);


