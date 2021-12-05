package com.epam.training.ticketservice.repository;

import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.model.ScreeningId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, ScreeningId> {
    List<Screening> findScreeningsById_Room_Name(String movieName);
}