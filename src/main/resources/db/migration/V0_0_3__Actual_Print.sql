CREATE TABLE IF NOT EXISTS current_print
(
    id         INTEGER  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    print_file INTEGER  NOT NULL,
    start_time DATETIME NOT NULL,
    FOREIGN KEY (print_file) REFERENCES print_files (id)
);