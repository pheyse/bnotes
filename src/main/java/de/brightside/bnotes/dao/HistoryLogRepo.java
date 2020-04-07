package de.brightside.bnotes.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import de.brightside.bnotes.model.HistoryLog;

public interface HistoryLogRepo extends JpaRepository<HistoryLog,Long>{
}