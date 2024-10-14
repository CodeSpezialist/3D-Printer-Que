package com.printer.fileque.repos;

import com.printer.fileque.entities.PrintFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintFileRepo extends JpaRepository<PrintFile, Long> {
}
