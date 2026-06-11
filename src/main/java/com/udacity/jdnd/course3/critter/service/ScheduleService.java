package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.schedule.Schedule;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PetService petService;
    private final EmployeeRepository employeeRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           PetService petService,
                           EmployeeRepository employeeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.petService = petService;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDTO.getDate());
        schedule.setActivities(scheduleDTO.getActivities() == null ? new HashSet<>() : new HashSet<>(scheduleDTO.getActivities()));
        schedule.setPets(loadPets(scheduleDTO.getPetIds()));
        schedule.setEmployees(loadEmployees(scheduleDTO.getEmployeeIds()));
        return toScheduleDTO(scheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAllByOrderByIdAsc().stream()
                .map(this::toScheduleDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getScheduleForPet(long petId) {
        return scheduleRepository.findByPetId(petId).stream()
                .map(this::toScheduleDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        return scheduleRepository.findByEmployeeId(employeeId).stream()
                .map(this::toScheduleDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        return scheduleRepository.findByCustomerId(customerId).stream()
                .map(this::toScheduleDTO)
                .toList();
    }

    private List<Pet> loadPets(List<Long> petIds) {
        List<Pet> pets = new ArrayList<>();
        if (petIds == null) {
            return pets;
        }
        for (Long petId : petIds) {
            pets.add(petService.findPet(petId));
        }
        return pets;
    }

    private List<Employee> loadEmployees(List<Long> employeeIds) {
        List<Employee> employees = new ArrayList<>();
        if (employeeIds == null) {
            return employees;
        }
        for (Long employeeId : employeeIds) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
            employees.add(employee);
        }
        return employees;
    }

    private ScheduleDTO toScheduleDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setActivities(schedule.getActivities());
        dto.setEmployeeIds(schedule.getEmployees().stream()
                .map(Employee::getId)
                .toList());
        dto.setPetIds(schedule.getPets().stream()
                .map(Pet::getId)
                .toList());
        return dto;
    }
}
