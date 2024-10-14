package com.printer.fileque.repos;

import com.printer.fileque.entities.CurrentPrint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentPrintRepo extends JpaRepository<CurrentPrint, Long> {
}
