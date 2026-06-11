package com.udacity.jdnd.course3.critter.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByOrderByIdAsc();

    @Query("select distinct s from Schedule s join s.pets p where p.id = :petId order by s.id")
    List<Schedule> findByPetId(long petId);

    @Query("select distinct s from Schedule s join s.employees e where e.id = :employeeId order by s.id")
    List<Schedule> findByEmployeeId(long employeeId);

    @Query("select distinct s from Schedule s join s.pets p where p.owner.id = :customerId order by s.id")
    List<Schedule> findByCustomerId(long customerId);
}
